package com.example.jetpacktest

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.Normalizer

//Small Data classes to hold retrofit data
data class ActivePlayer(
    val PlayerID: Int,
    val FirstName: String,
    val LastName: String,
)
data class PlayerInfo(
    val NbaDotComPlayerID: Int
)

class HeadshotHandler {
    private val databaseHandler = DatabaseHandler()
    private val useBiggerImage: Boolean = true
    private var imageUrlPrefix: String? = null
    private val apiKey = Keys.SportsDataAPIKey
    private val activePlayersUrl =
        "https://api.sportsdata.io/v3/nba/scores/json/PlayersActiveBasic?key=$apiKey"
    private var matchingPlayerId = -1 // -1 means no match found and to not bother calling next req
    private var nbaDotComPlayerId = -1
    object Const { // We use object since only way to make var constant
        //Const tag for logging
        const val TAG = "Headshot Handler"
    }
    //Used in fetchPlayerId/fetchImageUrl to get active players and their ID
    private var imgUrl = "DEFAULT"

    fun fetchImageId(playerName: String, onResult: (Int) -> Unit) {
        //First, attempt fetching from the database
        val noAccentPlayerName = removeAccents(playerName)
        databaseHandler.executeNbaDotComId(noAccentPlayerName) { result ->
            nbaDotComPlayerId = result
            if (nbaDotComPlayerId != -1) onResult(nbaDotComPlayerId)
            else {
                Log.d(Const.TAG, "Database Miss, Fetching Headshot via API...")
                fetchThroughApi(playerName) { apiResult ->
                    nbaDotComPlayerId = apiResult
                    onResult(nbaDotComPlayerId)
                }
            }
        }
    }

    private fun fetchThroughApi(playerName: String, onResult: (Int) -> Unit) {
        //TODO: This is rather slow when database misses, need to fix it in sprint 3
        val baseUrl = "https://api.sportsdata.io/v3/nba/scores/json/"
        //Before anything else, we must remove accents in the passed name and split it
        val noAccentPlayerName = removeAccents(playerName)
        val nameParts =
            noAccentPlayerName.split(" ".toRegex(), limit = 2)
        val firstName = nameParts.getOrNull(0) ?: ""
        val lastName = nameParts.getOrNull(1) ?: ""

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(ApiInterface::class.java)
        val retrofitGames = retrofitBuilder.getActivePlayers(Keys.SportsDataAPIKey)

        retrofitGames.enqueue(object : Callback<List<ActivePlayer>?> {
            override fun onResponse(call: Call<List<ActivePlayer>?>,
                                response: Response<List<ActivePlayer>?>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    for (activePlayer in responseBody) {
                        //Check for matching name
                        if (firstName.equals(activePlayer.FirstName, ignoreCase = true) &&
                        lastName.equals(activePlayer.LastName, ignoreCase = true)) {
                            Log.d("HeadshotHandler", "Match Found: ${activePlayer.PlayerID}")
                            //Make second retrofit request
                            val retrofitBuilder2 = Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create())
                                .baseUrl(baseUrl)
                                .build()
                                .create(ApiInterface::class.java)
                            val retrofitPlayers = retrofitBuilder2.getPlayerById(
                                playerId = activePlayer.PlayerID,
                                apiKey = Keys.SportsDataAPIKey
                            )
                            retrofitPlayers.enqueue(object : Callback<PlayerInfo?> {
                                override fun onResponse(
                                    call2: Call<PlayerInfo?>,
                                    response2: Response<PlayerInfo?>
                                ) {
                                    if (response2.isSuccessful) {
                                        //No need to loop this time, since only one JSON object
                                        val playerInfo = response2.body()!!
                                        Log.d("HeadshotHandler", "${playerInfo.NbaDotComPlayerID}")
                                        onResult(playerInfo.NbaDotComPlayerID)
                                        return
                                    }
                                    else {
                                        Log.d("HeadshotHandler",
                                            "Retrofit failure, 2nd req.: Unsuccessful Response")
                                    }
                                }
                                override fun onFailure(call2: Call<PlayerInfo?>, t2: Throwable) {
                                    Log.d("HeadshotHandler",
                                        "Retrofit failure, 2nd req: $t2")
                                }
                            })
                        }
                    }
                }
                else {
                    Log.d("HeadshotHandler", "Retrofit failure: Unsuccessful Response")
                }
            }
            override fun onFailure(call: Call<List<ActivePlayer>?>, t: Throwable) {
                Log.d("HeadshotHandler", "Retrofit failure: $t")

            }
        })
    }

    fun fetchImageUrl(playerName: String, context: Context, onResult: (String) -> Unit){
        imageUrlPrefix = if (useBiggerImage) "https://cdn.nba.com/headshots/nba/latest/1040x760/"
        else "https://ak-static.cms.nba.com/wp-content/uploads/headshots/nba/latest/260x190/"
        val request = JsonArrayRequest(
            Request.Method.GET, activePlayersUrl, null,
            { response ->
                try {
                    //First, remove accents using helper function, since API doesn't use accents
                    val noAccentPlayerName = removeAccents(playerName)
                    //Splits up name in first/last since API requires that
                    val nameParts =
                        noAccentPlayerName.split(" ".toRegex(), limit = 2)
                    val firstName = nameParts.getOrNull(0) ?: ""
                    val lastName = nameParts.getOrNull(1) ?: ""
                    matchingPlayerId = -1 // Default for if no id found (aka not active player)
                    //Loop through each JSONObject aka each player
                    for (i in 0 until response.length()) {
                        val player = response.getJSONObject(i) // Grab ith player
                        if (firstName.equals(
                                player.getString("FirstName"),
                                ignoreCase = true // Ignore casing of letters
                            ) &&
                            lastName.equals(player.getString("LastName"), ignoreCase = true)
                        ) {
                            matchingPlayerId = player.getInt("PlayerID")
                            Log.d(Const.TAG, "Player ID found!")
                            //New request uses the matching ID, also we pass in the API key again
                            val playerDetailUrl =
                                "https://api.sportsdata.io/v3/nba/scores/json/Player/" +
                                        matchingPlayerId + "?key=" + apiKey
                            // Call the second Volley request here within the first request's onResponse method
                            // If we tried doing it sequentially, it wouldn't work since the timing is async
                            val request2 = JsonObjectRequest(
                                Request.Method.GET, playerDetailUrl, null,
                                { response2 ->
                                    try {
                                        //Fetch player ID (might be null, if so set to -1)
                                        nbaDotComPlayerId = response2.optInt("NbaDotComPlayerID", -1)
                                        //As long as we didn't get null (-1), set imgUrl, otherwise default
                                        imgUrl = if (nbaDotComPlayerId != -1) "$imageUrlPrefix$nbaDotComPlayerId.png"
                                                else "DEFAULT" // Without this, null id's would have no fallback
                                        //Performs lambda callback to send back new imgUrl
                                        onResult(imgUrl)
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }
                            ) { error -> error.printStackTrace() }
                            //Use context we passed in
                            Volley.newRequestQueue(context.applicationContext).add(request2)
                            break // Break out of loop since match found
                        }
                    }
                    // If id still equals -1, then no match so we set to DEFAULT and callback
                    if (matchingPlayerId == -1) {
                        onResult("DEFAULT")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace() // Prints error in logcat
                }
            }) { error -> error.printStackTrace() } // Prints error in logcat
        //Use context we passed in
        Volley.newRequestQueue(context.applicationContext).add(request)
    }

    @Composable
    fun ComposeImage(imgToCompose: String, contentDesc: String,
                     width: Dp, height: Dp) {
        //Now, we display image depending on if we actually got a URL or not
        if (imgToCompose != "DEFAULT") {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = imgToCompose)
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                        }).build()
                ),
                //Change desc/width/height based off params
                contentDescription = contentDesc,
                modifier = Modifier
                    .width(width)
                    .height(height)
            )
        }
        else {
            Image(
                painter = painterResource(id = R.drawable.fallback),
                contentDescription = contentDesc,
                modifier = Modifier
                    .width(width)
                    .height(height)
            )
        }
    }

    //Niko: Helper function to remove accents from playerName, since API we use for headshots doesn't use accent
    //Got it from https://stackoverflow.com/a/3322174
    private fun removeAccents(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }

}
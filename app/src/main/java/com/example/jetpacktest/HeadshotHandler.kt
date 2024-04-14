package com.example.jetpacktest

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    private val apiKey = Keys.SPORTS_DATA_IO_KEY
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
                Log.d(Const.TAG, "Database Miss, Player is likely a 2023 rookie...")
                /*
                Log.d(Const.TAG, "Database Miss, Fetching Headshot via API...")
                fetchThroughApi(playerName) { apiResult ->
                    nbaDotComPlayerId = apiResult
                    onResult(nbaDotComPlayerId)
                }
                 */
            }
        }
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

    private fun fetchThroughApi(playerName: String, onResult: (Int) -> Unit) {
        //TODO: Useless now, since database covers everything up to this year, and this fails on this year, so commented out
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
        val retrofitGames = retrofitBuilder.getActivePlayers(Keys.SPORTS_DATA_IO_KEY)

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
                                apiKey = Keys.SPORTS_DATA_IO_KEY
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
}
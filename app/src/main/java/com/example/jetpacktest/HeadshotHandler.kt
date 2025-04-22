package com.example.jetpacktest

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Small Data classes to hold retrofit data
data class ActivePlayer(
    @SerializedName("PlayerID")
    val playerId: Int,
    @SerializedName("FirstName")
    val firstName: String,
    @SerializedName("LastName")
    val lastName: String,
)
data class PlayerInfo(
    @SerializedName("NbaDotComPlayerID")
    val nbaDotComPlayerId: Int
)

class HeadshotHandler {
    private val databaseHandler = DatabaseHandler()
    //private val apiKey = Keys.SPORTS_DATA_IO_KEY
    private var nbaDotComPlayerId = -1
    object Const {
        const val TAG = "Headshot Handler"
    }

    fun fetchImageId(playerName: String, onResult: (Int) -> Unit) {
        val noAccentPlayerName = removeAccents(playerName)
        databaseHandler.executeNbaDotComId(noAccentPlayerName) { result ->
            nbaDotComPlayerId = result
            if (nbaDotComPlayerId != -1) {
                onResult(nbaDotComPlayerId)
            }
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
                     width: Dp, height: Dp, modifier: Modifier = Modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(CircleShape)
                .then(modifier) // Custom parameter modifier for border
        ) {
            if (imgToCompose != "DEFAULT") {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data = imgToCompose)
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                            }).build()
                    ),
                    contentDescription = contentDesc,
                    modifier = Modifier
                        .width(width)
                        .height(height)
                        .clip(CircleShape)
                )
            } else { // Display fallback place holder in case of invalid URL
                Image(
                    painter = painterResource(id = R.drawable.fallback),
                    contentDescription = contentDesc,
                    modifier = Modifier
                        .width(width)
                        .height(height)
                )
            }
        }
    }
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
                        if (firstName.equals(activePlayer.firstName, ignoreCase = true) &&
                        lastName.equals(activePlayer.lastName, ignoreCase = true)) {
                            Log.d("HeadshotHandler", "Match Found: ${activePlayer.playerId}")
                            //Make second retrofit request
                            val retrofitBuilder2 = Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create())
                                .baseUrl(baseUrl)
                                .build()
                                .create(ApiInterface::class.java)
                            val retrofitPlayers = retrofitBuilder2.getPlayerById(
                                playerId = activePlayer.playerId,
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
                                        Log.d("HeadshotHandler", "${playerInfo.nbaDotComPlayerId}")
                                        onResult(playerInfo.nbaDotComPlayerId)
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
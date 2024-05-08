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
}
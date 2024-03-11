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
import java.text.Normalizer

class HeadshotHandler {
    private val useBiggerImage: Boolean = true
    private var imageUrlPrefix: String? = null
    private val activePlayersUrl =
        "https://api.sportsdata.io/v3/nba/scores/json/PlayersActiveBasic?key=" +
                "010c01189ccd41809218da51a0850ac8"
    private var matchingPlayerId = -1 // -1 means no match found and to not bother calling next request
    private var nbaDotComPlayerId = -1
    object Const { // We use object since only way to make var constant
        //Const tag for logging
        const val TAG = "ProfileActivity"
    }
    //Used in fetchPlayerId/fetchImageUrl to get active players and their ID
    private var imgUrl = "DEFAULT"



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
                        noAccentPlayerName.split(" ".toRegex(), limit = 2).toTypedArray()
                    val firstName = nameParts[0]
                    val lastName = nameParts[1]
                    matchingPlayerId = -1 // Default for if no id found (aka not active player)
                    //Loop through each JSONObject aka each player
                    for (i in 0 until response.length()) {
                        val playerObject = response.getJSONObject(i) // Grab ith player
                        if (firstName.equals(
                                playerObject.getString("FirstName"),
                                ignoreCase = true // Ignore casing of letters
                            ) &&
                            lastName.equals(playerObject.getString("LastName"), ignoreCase = true)
                        ) {
                            matchingPlayerId = playerObject.getInt("PlayerID")
                            Log.d(Const.TAG, "Player ID found!")
                            //New request uses the matching ID, also we pass in the API key again
                            val playerDetailUrl =
                                "https://api.sportsdata.io/v3/nba/scores/json/Player/" +
                                        matchingPlayerId + "?key=" +
                                        "010c01189ccd41809218da51a0850ac8"
                            // Call the second Volley request here within the first request's onResponse method
                            // If we tried doing it sequentially, it wouldn't work since the timing is async
                            val request2 = JsonObjectRequest(
                                Request.Method.GET, playerDetailUrl, null,
                                { response2 ->
                                    try {
                                        //Grabs NBA's site player ID
                                        nbaDotComPlayerId = response2.getInt("NbaDotComPlayerID")
                                        //Creates our new imgUrl using the NBA ID
                                        imgUrl =
                                            "$imageUrlPrefix$nbaDotComPlayerId.png"
                                        //Performs lambda callback using our new imgUrl to
                                        //communicate back to ProfileScreen that we got a url
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
                        imgUrl = "DEFAULT"
                        onResult(imgUrl)
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
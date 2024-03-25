package com.example.jetpacktest

import android.content.Context
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.jetpacktest.models.Game
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GamesHandler {
    private val baseGameUrl = "https://api.balldontlie.io/v1/games"
    val gamesList = mutableListOf<Game>() // To hold list of games

    fun fetchDailyGames(currDate: Date, context: Context, onResult: (MutableList<Game>) -> Unit) {
        //We first must convert our date into a usable format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(currDate)
        //Now, create full endpoint URL by appending this date
        val gameUrl = "$baseGameUrl?dates[]=$formattedDate"
        //We can now make our JSON request
        val request = object : JsonObjectRequest(
            Method.GET, gameUrl, null,
            { response ->
                try {
                    //First, grab array of all games that day
                    val arrayGames = response.getJSONArray("data")
                    //Loop through each game (each JSON object)
                    for (i in 0 until arrayGames.length()) {
                        val gameObject = arrayGames.getJSONObject(i)
                        //Inside each game object, we must unpack another two objects
                        val homeTeamObject = gameObject.getJSONObject("home_team")
                        val awayTeamObject = gameObject.getJSONObject("visitor_team")
                        //We grab game status/time since it will require additional logic
                        var gameStatus = gameObject.getString("status")
                        var gameTime = gameObject.getString("time")
                        //gameTime set to Final means game ended, null means hasn't started
                        if (gameTime == "Final" || gameTime == "null") gameTime = ""
                        //However, gameStatus returns a long string if the game hasn't started:
                        if (gameStatus.startsWith("20")) {
                            // If game status started with long string (year), then convert
                            gameStatus = fetchGameTime(gameStatus)
                        }
                        //Now, we can instantiate a new game object using all the data above/inside
                        val game = Game(
                            homeName = homeTeamObject.getString("name"),
                            awayName = awayTeamObject.getString("name"),
                            homeScore = gameObject.getInt("home_team_score").toString(),
                            awayScore = gameObject.getInt("visitor_team_score").toString(),
                            gameStatus = gameStatus,
                            gameTime = gameTime,
                            homeLogo = 0,
                            awayLogo = 0
                        )
                        // Finally, we add to the list of games
                        gamesList.add(game)
                    }
                    // We can now callback to the invoking function our complete list of games
                    onResult(gamesList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            }) {
            //Override getHeaders() to include authentication header for key
            //Niko: Found this here -  https://stackoverflow.com/a/53141982
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = Keys.BDLAPIKey
                return headers
            }
        }
        //Use context we passed in
        Volley.newRequestQueue(context.applicationContext).add(request)
    }

    //Used in the event game hasn't started, to convert game status to time it starts
    private fun fetchGameTime(unformattedDate: String): String {
        //TODO: Implement functionality
        return unformattedDate
    }
}
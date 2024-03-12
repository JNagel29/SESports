package com.example.jetpacktest

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class TeamHandler {
    //API Key and prefix of URL (we will append the team abbreviation, and key in fetchTeamPlayers)
    private val apiKey = "010c01189ccd41809218da51a0850ac8"
    private val teamPlayersUrlPrefix = "https://api.sportsdata.io/v3/nba/scores/json/PlayersBasic/"
    private var playerNames = mutableListOf<String>()
    fun fetchTeamPlayers(teamAbbrev: String, context: Context, onResult: (MutableList<String>) -> Unit) {
        val teamPlayersUrl = "$teamPlayersUrlPrefix$teamAbbrev?key=$apiKey"
        val request = JsonArrayRequest(
            Request.Method.GET, teamPlayersUrl, null,
            { response ->
                try {
                    //Parse through list of playerNames via function below
                    playerNames = parsePlayersJsonResponse(response)
                    //Return back these names using our callback function (needed b/c async)
                    onResult(playerNames)
                } catch (e: JSONException) {
                    e.printStackTrace() // Prints error in logcat
                }
            }) { error -> error.printStackTrace() } // Prints error in logcat
        //Use context we passed in
        Volley.newRequestQueue(context.applicationContext).add(request)
    }

    private fun parsePlayersJsonResponse(jsonArray: JSONArray): MutableList<String> {
        val playerNames = mutableListOf<String>()

        for (i in 0 until jsonArray.length()) {
            //Loop through array and get the ith JSON object
            val playerObject = jsonArray.getJSONObject(i)
            //Then, retrieve the firstName/lastName fields and append for full name
            val firstName = playerObject.getString("FirstName")
            val lastName = playerObject.getString("LastName")
            val fullName = "$firstName $lastName"
            //Add fullName list of playerNames for that team
            playerNames.add(fullName)
        }
        //Return list of playerNames, which fetch will return to our TeamProfileScreen to display
        return playerNames;
    }
}
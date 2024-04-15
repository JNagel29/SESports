package com.example.jetpacktest

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

// Used in ProfileScreen.kt when the user selects 2024 as chosenYear, in order to get current API stats
class ApiHandler {

    // Method to fetch player data
    fun fetchPlayerData(context:Context, onResult: (String) -> Unit) {
        // Initialize RequestQueue
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val playerId = 19
        val playerUrl = "https://api.balldontlie.io/v1/players/$playerId"

        // adds API key to the request header
        val apiKey = "be7d1cb9-bffe-4235-9987-bdd2b91164a0"
        //We can now make our JSON request
        val request = object : JsonObjectRequest(
            Method.GET, playerUrl, null,
            { response ->
                try {
                    val playerData = response.getJSONObject("data")

                    val firstName = playerData.getString("first_name")
                    val lastName = playerData.getString("last_name")
                    val playerNameText = "$firstName $lastName"

                    val season = "2024"
                    val teamFullName = playerData.getJSONObject("team").getString("full_name")

                    // Update UI or perform further actions with fetched data
                    onResult("$playerNameText $season $teamFullName")
                    /*
                    onResult(Player(
                        name = playerNameText,
                    year = resultSet.getInt("Year"),
                    position = resultSet.getString("Position"),
                    team = "",
                    points = resultSet.getFloat("PTS"),
                    assists = resultSet.getFloat("AST"),
                    steals = resultSet.getFloat("STL"),
                    blocks = resultSet.getFloat("BLK"),
                    totalRebounds = resultSet.getFloat("TRB"),
                    turnovers = resultSet.getFloat("TOV"),
                    personalFouls = resultSet.getFloat("PF"),
                    minutesPlayed = resultSet.getFloat("MP"),
                    fieldGoals = resultSet.getFloat("FG"),
                    fieldGoalAttempts = resultSet.getFloat("FGA"),
                    fieldGoalPercent = resultSet.getFloat("FG_PERCENT"),
                    threePointers = resultSet.getFloat("3P"),
                    threePointerAttempts = resultSet.getFloat("3PA"),
                    threePointPercent = resultSet.getFloat("3P_PERCENT"),
                    twoPointers = resultSet.getFloat("2P"),
                    twoPointerAttempts = resultSet.getFloat("2PA"),
                    twoPointPercent = resultSet.getFloat("2P_PERCENT"),
                    effectiveFieldGoalPercent = resultSet.getFloat("eFG_PERCENT"),
                    offensiveRebounds = resultSet.getFloat("ORB"),
                    defensiveRebounds = resultSet.getFloat("DRB")
                    )

                     */


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

}

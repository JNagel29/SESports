package com.example.jetpacktest

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.jetpacktest.common.Keys
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class TeamPlayer (
    val BirthCity: String,
    val BirthCountry: String,
    val BirthDate: String,
    val BirthState: String,
    val FirstName: String,
    val GlobalTeamID: Int,
    val Height: Int,
    val Jersey: Int,
    val LastName: String,
    val PlayerID: Int,
    val Position: String,
    val PositionCategory: String,
    val SportsDataID: String,
    val Status: String,
    val Team: String,
    val TeamID: Int,
    val Weight: Int
)

class TeamHandler {
    //API Key and prefix of URL (we will append the team abbreviation, and key in fetchTeamPlayers)
    private val apiKey = Keys.SportsDataAPIKey
    private val teamPlayersUrlPrefix = "https://api.sportsdata.io/v3/nba/scores/json/PlayersBasic/"
    private var playerRows = mutableListOf<String>()

    fun fetchCurrentRoster(teamAbbrev: String, onResult: (MutableList<TeamPlayer>) -> Unit) {
        val basePlayersByTeamUrl = "https://api.sportsdata.io/v3/nba/scores/json/"
        val teamPlayersList = mutableListOf<TeamPlayer>()
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(basePlayersByTeamUrl)
            .build()
            .create(ApiInterface::class.java)
        val retrofitPlayers = retrofitBuilder.getPlayersByTeam(
            team = teamAbbrev,
            apiKey = Keys.SportsDataAPIKey
        )
        retrofitPlayers.enqueue(object : Callback<List<TeamPlayer>?> {
            override fun onResponse(call: Call<List<TeamPlayer>?>, response: Response<List<TeamPlayer>?>) {
                Log.d("TeamHandler", "Fetching list of players...")
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    for (teamPlayer in responseBody) teamPlayersList.add(teamPlayer)
                    onResult(teamPlayersList)
                }
                else {
                    Log.d("TeamHandler", "Retrofit failure: Unsuccessful Response")
                }
            }
            override fun onFailure(call: Call<List<TeamPlayer>?>, t: Throwable) {
                Log.d("TeamHandler", "Retrofit failure: $t")            }
        })
    }

    fun fetchTeamPlayers(teamAbbrev: String, context: Context, onResult: (MutableList<String>) -> Unit) {
        val teamPlayersUrl = "$teamPlayersUrlPrefix$teamAbbrev?key=$apiKey"
        val request = JsonArrayRequest(
            Request.Method.GET, teamPlayersUrl, null,
            { response ->
                try {
                    //Parse through list of playerNames via function below
                    playerRows = parsePlayersJsonResponse(response)
                    //Return back these names using our callback function (needed b/c async)
                    onResult(playerRows)
                } catch (e: JSONException) {
                    e.printStackTrace() // Prints error in logcat
                }
            }) { error -> error.printStackTrace() } // Prints error in logcat
        //Use context we passed in
        Volley.newRequestQueue(context.applicationContext).add(request)
    }

    private fun parsePlayersJsonResponse(jsonArray: JSONArray): MutableList<String> {
        for (i in 0 until jsonArray.length()) {
            //Loop through array and get the ith JSON object
            val player = jsonArray.getJSONObject(i)
            //Then, retrieve the firstName/lastName fields and append for full name
            val firstName = player.getString("FirstName")
            val lastName = player.getString("LastName")
            val jerseyNum = player.optInt("Jersey", -1)
            val position = player.getString("Position")
            // Check if jerseyNum was -1 aka null, if so, replace it with "N/A"
            val jerseyText = if (jerseyNum == -1) "N/A" else "#$jerseyNum"
            val playerRow = "$firstName $lastName - $jerseyText - $position"
            playerRows.add(playerRow)
        }
        return playerRows
    }
}
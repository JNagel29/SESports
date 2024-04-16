package com.example.jetpacktest

import android.util.Log
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class TeamPlayer (
    @SerializedName("BirthCity")
    val birthCity: String,
    @SerializedName("BirthCountry")
    val birthCountry: String,
    @SerializedName("BirthDate")
    val birthDate: String,
    @SerializedName("BirthState")
    val birthState: String,
    @SerializedName("FirstName")
    val firstName: String,
    @SerializedName("GlobalTeamID")
    val globalTeamId: Int,
    @SerializedName("Height")
    val height: Int,
    @SerializedName("Jersey")
    val jersey: Int,
    @SerializedName("LastName")
    val lastName: String,
    @SerializedName("PlayerID")
    val playerId: Int,
    @SerializedName("Position")
    val position: String,
    @SerializedName("PositionCategory")
    val positionCategory: String,
    @SerializedName("SportsDataID")
    val sportsDataId: String,
    @SerializedName("Status")
    val status: String,
    @SerializedName("Team")
    val team: String,
    @SerializedName("TeamID")
    val teamId: Int,
    @SerializedName("Weight")
    val weight: Int
)

class TeamHandler {
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
            apiKey = Keys.SPORTS_DATA_IO_KEY
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
}
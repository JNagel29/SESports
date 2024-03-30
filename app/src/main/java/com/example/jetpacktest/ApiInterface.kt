package com.example.jetpacktest

import com.example.jetpacktest.models.GameResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @Headers("Authorization: ${Keys.BDLAPIKey}")
    @GET("games")
    fun getGames(@Query("dates[]") date: String): Call<GameResponse>

    @GET("PlayersActiveBasic")
    fun getActivePlayers(@Query("key") apiKey: String): Call<List<ActivePlayer>>

    @GET("Player/{playerId}")
    fun getPlayerById(@Path("playerId") playerId: Int, @Query("key") apiKey: String)
        :Call<PlayerInfo>

    @GET("PlayersBasic/{team}")
    fun getPlayersByTeam(@Path("team") team: String, @Query("key") apiKey: String) : Call<List<TeamPlayer>>
}
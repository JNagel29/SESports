package com.example.jetpacktest

import com.example.jetpacktest.models.GameResponse
import com.example.jetpacktest.models.InjuredPlayer
import com.example.jetpacktest.models.PlayerPersonalInfo
import com.example.jetpacktest.models.RestPlayer
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @Headers("Authorization: ${Keys.BDL_API_KEY}")
    @GET("games")
    fun getGames(@Query("dates[]") date: String): Call<GameResponse>

    @Headers("Authorization: ${Keys.BDL_API_KEY}")
    @GET("players")
    suspend fun getPersonalInfo(
        @Query("first_name") firstName: String,
        @Query("last_name") lastName: String
    ): PlayerPersonalInfo

    @GET("PlayersActiveBasic")
    fun getActivePlayers(@Query("key") apiKey: String): Call<List<ActivePlayer>>

    @GET("Player/{playerId}")
    fun getPlayerById(@Path("playerId") playerId: Int, @Query("key") apiKey: String)
            :Call<PlayerInfo>

    @GET("PlayersBasic/{team}")
    fun getPlayersByTeam(@Path("team") team: String, @Query("key") apiKey: String) :
            Call<List<TeamPlayer>>

    @GET("InjuredPlayers")
    suspend fun getInjuredPlayers(@Query("key") apiKey: String) : List<InjuredPlayer>

    //Endpoints to fetch DB data from REST API
    @GET("player")
    suspend fun getPlayerByNameAndYear(@Query("name") name: String, @Query("year") year: Int)
            : List<RestPlayer>
    @GET("search-players")
    suspend fun getPlayerSearchResults(@Query("name") name: String): List<String>
}
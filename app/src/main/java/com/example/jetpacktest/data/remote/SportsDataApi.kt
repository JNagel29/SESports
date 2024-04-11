package com.example.jetpacktest.data.remote

import com.example.jetpacktest.common.Keys
import com.example.jetpacktest.data.remote.dto.active_players.ActivePlayerDto
import com.example.jetpacktest.data.remote.dto.player_info.PlayerInfoDto
import com.example.jetpacktest.data.remote.dto.team_player.TeamPlayerDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SportsDataApi {
    @GET("PlayersActiveBasic")
    suspend fun getActivePlayers(
        @Query("key") apiKey: String = Keys.SportsDataAPIKey
    ): List<ActivePlayerDto>

    @GET("Player/{playerId}")
    suspend fun getPlayerById(
        @Path("playerId") playerId: Int,
        @Query("key") apiKey: String = Keys.SportsDataAPIKey
    ): PlayerInfoDto

    @GET("PlayersBasic/{team}")
    suspend fun getPlayersByTeam(
        @Path("team") team: String,
        @Query("key") apiKey: String = Keys.SportsDataAPIKey
    ): List<TeamPlayerDto>
}
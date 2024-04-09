package com.example.jetpacktest.data.repository

import com.example.jetpacktest.data.remote.SportsDataApi
import com.example.jetpacktest.data.remote.dto.active_players.ActivePlayerDto
import com.example.jetpacktest.data.remote.dto.player_info.PlayerInfoDto
import com.example.jetpacktest.domain.repository.HeadshotRepository
import javax.inject.Inject

class HeadshotRepositoryImpl @Inject constructor(
    private val api: SportsDataApi
) : HeadshotRepository {

    override suspend fun getActivePlayers(apiKey: String): List<ActivePlayerDto> {
        return api.getActivePlayers(apiKey = apiKey)
    }

    override suspend fun getPlayerById(playerId: Int, apiKey: String): PlayerInfoDto {
        return api.getPlayerById(playerId = playerId, apiKey = apiKey)
    }

}
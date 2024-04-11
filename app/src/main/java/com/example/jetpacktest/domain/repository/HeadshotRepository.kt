package com.example.jetpacktest.domain.repository

import com.example.jetpacktest.data.remote.dto.active_players.ActivePlayerDto
import com.example.jetpacktest.data.remote.dto.player_info.PlayerInfoDto

interface HeadshotRepository {

    suspend fun getActivePlayers(): List<ActivePlayerDto>
    suspend fun getPlayerById(playerId: Int): PlayerInfoDto
}
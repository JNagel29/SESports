package com.example.jetpacktest.domain.repository

import com.example.jetpacktest.data.remote.dto.team_player.TeamPlayerDto

interface TeamRepository {

    suspend fun getPlayersByTeam(team: String): List<TeamPlayerDto>
}
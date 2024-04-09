package com.example.jetpacktest.data.repository

import com.example.jetpacktest.data.remote.SportsDataApi
import com.example.jetpacktest.data.remote.dto.team_player.TeamPlayerDto
import com.example.jetpacktest.domain.repository.TeamRepository
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val api: SportsDataApi
) : TeamRepository {

    override suspend fun getPlayersByTeam(team: String, apiKey: String): List<TeamPlayerDto> {
        return api.getPlayersByTeam(team = team, apiKey = apiKey)
    }

}
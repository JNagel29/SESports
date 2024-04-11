package com.example.jetpacktest.data.repository

import android.util.Log
import com.example.jetpacktest.data.remote.SportsDataApi
import com.example.jetpacktest.data.remote.dto.team_player.TeamPlayerDto
import com.example.jetpacktest.domain.repository.TeamRepository
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val api: SportsDataApi
) : TeamRepository {

    override suspend fun getPlayersByTeam(team: String): List<TeamPlayerDto> {
        Log.d("TeamRepository", "Fetching new roster...")
        return api.getPlayersByTeam(team = team)
    }

}
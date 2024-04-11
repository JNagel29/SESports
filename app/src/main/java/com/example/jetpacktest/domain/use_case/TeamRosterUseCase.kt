package com.example.jetpacktest.domain.use_case

import com.example.jetpacktest.common.Resource
import com.example.jetpacktest.data.remote.dto.team_player.toTeamPlayer
import com.example.jetpacktest.domain.model.TeamPlayer
import com.example.jetpacktest.domain.repository.TeamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class TeamRosterUseCase @Inject constructor(
    private val repository: TeamRepository
) {
    operator fun invoke(teamAbbrev: String):
            Flow<Resource<List<TeamPlayer>>> = flow {
        try {
            emit(Resource.Loading())
            val teamPlayers = repository.getPlayersByTeam(team = teamAbbrev)
                .map { it.toTeamPlayer() }
            emit(Resource.Success(teamPlayers))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check internet connection"))
        }
    }
}
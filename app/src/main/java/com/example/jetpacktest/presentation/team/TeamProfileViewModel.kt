package com.example.jetpacktest.presentation.team

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.common.Constants
import com.example.jetpacktest.common.Resource
import com.example.jetpacktest.domain.use_case.TeamRosterUseCase
import com.example.jetpacktest.models.NbaTeam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TeamProfileViewModel @Inject constructor(
    private val teamRosterUseCase: TeamRosterUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _state = mutableStateOf(TeamProfileState())
    val state: State<TeamProfileState> = _state

    init {
        //Fetch the correct ID from savedStateHandle bundle
        savedStateHandle.get<String>(Constants.PARAM_TEAM_PROFILE_NAME)?.let { teamName ->
            val teamAbbrev = NbaTeam.namesToAbbreviations[teamName] ?: teamName
            //getTeamPlayers(teamName = teamName, teamAbbrev = teamAbbrev)
        }
    }

    private fun getTeamPlayers(teamName: String, teamAbbrev: String) {
        teamRosterUseCase(teamAbbrev = teamAbbrev).onEach { result ->
            when(result) {
                is Resource.Success -> {
                    _state.value = TeamProfileState(
                        teamPlayers = (result.data ?: emptyList()),
                        teamName = teamName
                    )
                }
                is Resource.Error -> {
                    _state.value = TeamProfileState(error = result.message ?: "Unexpected error")
                }
                is Resource.Loading -> {
                    _state.value = TeamProfileState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope) // Very important...don't forget it!
    }
}
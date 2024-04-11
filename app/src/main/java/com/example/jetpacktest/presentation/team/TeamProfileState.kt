package com.example.jetpacktest.presentation.team

import com.example.jetpacktest.domain.model.TeamPlayer

data class TeamProfileState(
    val teamPlayers: List<TeamPlayer> = emptyList(), // List of team players
    val teamName: String = "",
    val isLoading: Boolean = false, // Loading indicator
    val error: String = "" // Error message if any
)

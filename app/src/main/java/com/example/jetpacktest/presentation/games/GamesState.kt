package com.example.jetpacktest.presentation.games

import com.example.jetpacktest.data.remote.dto.games.Game
import com.example.jetpacktest.domain.model.Games

data class GamesState(
    val games: Games = Games(data = emptyList()), // List of games
    val isLoading: Boolean = false, // Loading indicator
    val error: String = "" // Error message if any
)

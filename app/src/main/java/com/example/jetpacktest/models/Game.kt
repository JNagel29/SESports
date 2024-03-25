package com.example.jetpacktest.models

//Data class to represent each game, used in GamesScreen
data class Game(
    val homeName: String,
    val awayName: String,
    val homeScore: String,
    val awayScore: String,
    val gameStatus: String,
    val gameTime: String,
    val homeLogo: Int,
    val awayLogo: Int
)
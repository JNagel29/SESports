package com.example.jetpacktest.models

data class BaseballPlayerSeasonStats(
    val PlayerID: Int,
    val Name: String,
    val Team: String,
    val Position: String,
    val Games: Int,
    val AtBats: Int,
    val Hits: Int,
    val HomeRuns: Int,
    val RunsBattedIn: Int,
    val BattingAverage: Double
    // Add more fields as needed
)

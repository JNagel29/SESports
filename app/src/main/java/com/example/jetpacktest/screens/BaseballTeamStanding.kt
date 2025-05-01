package com.example.jetpacktest.models

data class BaseballTeamStanding(
    val rank: Int,
    val name: String,
    val wins: Int,
    val losses: Int,
    val winLossPercentage: Float,
    val league: League,
    val abbrev: String,
    val logoUrl: String, // Use this if you're pulling logo from database/API
    val season: Int // Optional if you want to display or filter by season
) {
    enum class League {
        AMERICAN,
        NATIONAL
    }
}

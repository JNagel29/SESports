package com.example.jetpacktest.models

data class TeamStanding(
    val rank: Int,
    val name: String,
    val wins: Int,
    val losses: Int,
    val winLossPercentage: Float
)

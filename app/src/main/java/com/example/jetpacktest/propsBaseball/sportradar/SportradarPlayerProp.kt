package com.example.jetpacktest.propsBaseball.sportradar

data class SportradarPlayerProp(
    val playerName: String,
    val team: String,
    val stat: String,
    val outcomes: List<SportradarOutcome>
)


data class SportradarOutcome(
    val sportsbook: String,
    val line: Double,
    val odds: String
)

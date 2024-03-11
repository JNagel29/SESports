package com.example.jetpacktest.models

data class Player(
    //Data for players fetched from DB
    val name: String,
    val year: Int,
    val position: String,
    val team: String,
    val points: Float,
    val assists: Float,
    val steals: Float,
    val blocks: Float,
    val totalRebounds: Float,
    val turnovers: Float,
    val personalFouls: Float,
    val minutesPlayed: Float,
    val fieldGoals: Float,
    val fieldGoalAttempts: Float,
    val fieldGoalPercent: Float,
    val threePointers: Float,
    val threePointerAttempts: Float,
    val threePointPercent: Float,
    val twoPointers: Float,
    val twoPointerAttempts: Float,
    val twoPointPercent: Float,
    val effectiveFieldGoalPercent: Float,
    val offensiveRebounds: Float,
    val defensiveRebounds: Float,
) {
    //Secondary constructor, default for ()
    constructor() : this("", -1, "", "", -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
        -1.0f)
}

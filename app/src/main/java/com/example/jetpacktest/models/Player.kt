package com.example.jetpacktest.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//Data Class to hold data fetched from DB/API
@Parcelize
data class Player(
    val name: String,
    val year: Int,
    val position: String,
    var team: String, // var b/c we need to append to it
    val points: Float,
    val assists: Float,
    val steals: Float,
    val blocks: Float,
    val totalRebounds: Float,
    val turnovers: Float,
    val personalFouls: Float,
    val minutesPlayed: Float,
    val gamesStarted: Int,
    val fieldGoals: Float,
    val fieldGoalAttempts: Float,
    val fieldGoalPercent: Float,
    val threePointers: Float,
    val threePointerAttempts: Float,
    val threePointPercent: Float,
    val twoPointers: Float,
    val twoPointerAttempts: Float,
    val twoPointPercent: Float,
    val freeThrows: Float,
    val freeThrowAttempts: Float,
    val freeThrowPercent: Float,
    val effectiveFieldGoalPercent: Float,
    val offensiveRebounds: Float,
    val defensiveRebounds: Float,
) : Parcelable {
    //Secondary constructor, default for ()
    constructor() : this("", -1, "", "", -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0, -1.0f,
        -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f, -1.0f)
}

package com.example.jetpacktest.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class Pitcher(
    val id: Int = 0,
    val team: String = "",
    val playerName: String = "",
    val position: String = "",
    val season: Int = 0,
    val airOuts: Int = 0,
    val atBats: Int = 0,
    val avg: Double = 0.0,
    val balks: Int = 0,
    val baseOnBalls: Int = 0,
    val battersFaced: Int = 0,
    val blownSaves: Int = 0,
    val catchersInterference: Int = 0,
    val caughtStealing: Int = 0,
    val completeGames: Int = 0,
    val doubles: Int = 0,
    val earnedRuns: Int = 0,
    val era: Double = 0.0,
    val gamesFinished: Int = 0,
    val gamesPitched: Int = 0,
    val gamesPlayed: Int = 0,
    val gamesStarted: Int = 0,
    val groundIntoDoublePlay: Int = 0,
    val groundOuts: Int = 0,
    val groundOutsToAirouts: Double = 0.0,
    val hitBatsmen: Int = 0,
    val hitByPitch: Int = 0,
    val hits: Int = 0,
    val hitsPer9Inn: Double = 0.0,
    val holds: Int = 0,
    val homeRuns: Int = 0,
    val homeRunsPer9: Double = 0.0,
    val inheritedRunners: Int = 0,
    val inheritedRunnersScored: Int = 0,
    val inningsPitched: Double = 0.0,
    val intentionalWalks: Int = 0,
    val losses: Int = 0,
    val numberOfPitches: Int = 0,
    val obp: Double = 0.0,
    val ops: Double = 0.0,
    val outs: Int = 0,
    val pickoffs: Int = 0,
    val pitchesPerInning: Double = 0.0,
    val runs: Int = 0,
    val runsScoredPer9: Double = 0.0,
    val sacBunts: Int = 0,
    val sacFlies: Int = 0,
    val saveOpportunities: Int = 0,
    val saves: Int = 0,
    val shutouts: Int = 0,
    val slg: Double = 0.0,
    val stolenBasePercentage: Double = 0.0,
    val stolenBases: Int = 0,
    val strikeOuts: Int = 0,
    val strikePercentage: Double = 0.0,
    val strikeoutWalkRatio: Double = 0.0,
    val strikeoutsPer9Inn: Double = 0.0,
    val strikes: Int = 0,
    val totalBases: Int = 0,
    val triples: Int = 0,
    val walksPer9Inn: Double = 0.0,
    val whip: Double = 0.0,
    val wildPitches: Int = 0,
    val winPercentage: Double = 0.0,
    val wins: Int = 0,
    val war: BigDecimal? = BigDecimal.ZERO
) : Parcelable

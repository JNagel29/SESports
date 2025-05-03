package com.example.jetpacktest

import com.example.jetpacktest.models.Batter
import com.example.jetpacktest.models.Pitcher
import com.example.jetpacktest.models.Player
import java.text.Normalizer
import java.math.BigDecimal
//Niko: Helper function to remove accents from playerName, since API we use needs raw
//Got it from https://stackoverflow.com/a/3322174
fun removeAccents(input: String): String {
    return Normalizer.normalize(input, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
}

fun getFantasyRating(player: Player): String {
    var fantasyRating = 0f

    // Three Point Field Goals: 3 points
    fantasyRating += player.threePointers * 3

    // Two Point Field Goals: 2 points
    fantasyRating += player.twoPointers * 2

    // Free Throws Made: 1 point
    fantasyRating += player.freeThrows

    // Rebounds: 1.2 points
    fantasyRating += player.totalRebounds * 1.2f

    // Assists: 1.5 points
    fantasyRating += player.assists * 1.5f

    // Blocked Shots: 2 points
    fantasyRating += player.blocks * 2

    // Steals: 2 points
    fantasyRating += player.steals * 2

    // Turnovers: -1 points
    fantasyRating -= player.turnovers

    return "%.2f".format(fantasyRating)
}
fun dropZeroBeforeDecimal(input: Float): String {
    val inputStr = input.toString()
    return if (inputStr.startsWith("0.")) {
        inputStr.drop(1)
    } else {
        inputStr
    }
}

fun calculatePerformanceScore(player: Player): Float {
    var score = 0f
    score += player.points
    score += player.assists * 1.5f
    score += player.totalRebounds * 1.2f
    score += player.steals * 2f
    score += player.blocks * 2f
    score -= player.turnovers
    return score
}

fun evaluateSalaryPerformance(player: Player, salary: Float): String {
    val performance = calculatePerformanceScore(player)
    val valueRatio = performance / (salary / 1_000_000f)
    println("Player: ${player.name} | Value Ratio: %.2f".format(valueRatio))

    return when {
        valueRatio > 1.3 -> "Undervalued"
        valueRatio < 0.4 -> "Overvalued"
        else -> "Fair"
    }
}

fun evaluateBaseballPlayer(batter: Batter?, pitcher: Pitcher?, teamPayroll: Float): String {
    val performanceScore: Double = when {
        batter != null -> {
            val hr = batter.homeRuns?.toDouble() ?: 0.0
            val rbi = batter.rbi?.toDouble() ?: 0.0
            val runs = batter.runs?.toDouble() ?: 0.0
            val sb = batter.stolenBases?.toDouble() ?: 0.0
            val avg = batter.avg ?: 0.0
            val war = (batter.war ?: BigDecimal.ZERO).multiply(BigDecimal.valueOf(100)).toDouble()
            val cs = batter.caughtStealing?.toDouble() ?: 0.0
            val so = batter.strikeOuts?.toDouble() ?: 0.0

            hr * 4 + rbi * 3 + runs * 2.5 + sb * 1.5 + avg * 10 + war + cs * -0.5 + so * -1.5
        }

        pitcher != null -> {
            val wins = pitcher.wins?.toDouble() ?: 0.0
            val saves = pitcher.saves?.toDouble() ?: 0.0
            val so = pitcher.strikeOuts?.toDouble() ?: 0.0
            val eraAdj = (4.0 - (pitcher.era ?: 0.0)) * 8
            val holds = pitcher.holds?.toDouble() ?: 0.0
            val whip = pitcher.whip ?: 0.0
            val war = (pitcher.war ?: BigDecimal.ZERO).multiply(BigDecimal.valueOf(100)).toDouble()
            val losses = pitcher.losses?.toDouble() ?: 0.0
            val walksPer9 = pitcher.walksPer9Inn?.toDouble() ?: 0.0

            wins * 4 + saves * 3 + so * 1.5 + eraAdj + holds * 0.8 + war +
                    losses * -1.0 + walksPer9 * -2.0 + whip * -15
        }

        else -> return "Invalid"
    }

    val playerName = batter?.playerName ?: pitcher?.playerName ?: "Unknown"
    val payrollInMillions = teamPayroll / 1_000_000f
    val valueRatio = performanceScore / payrollInMillions
    println("Player: $playerName | Value Ratio: %.2f".format(valueRatio))

    return when {
        valueRatio > 8.0 -> "Underpaid"
        valueRatio < 4.0 -> "Overpaid"
        else -> "Fair"
    }
}

package com.example.jetpacktest

import com.example.jetpacktest.models.Player
import java.text.Normalizer

//Niko: Helper function to remove accents from playerName, since API we use needs raw
//Got it from https://stackoverflow.com/a/3322174
fun removeAccents(input: String): String {
    return Normalizer.normalize(input, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
}

fun dropZeroBeforeDecimal(input: Float): String {
    val inputStr = input.toString()
    return if (inputStr.startsWith("0.")) {
        inputStr.drop(1)
    } else {
        inputStr
    }
}
fun getFantasyScore(player: Player): String {
    var fantasyScore = 0f

    // Three Point Field Goals: 3 points
    fantasyScore += player.threePointers * 3

    // Two Point Field Goals: 2 points
    fantasyScore += player.twoPointers * 2

    // Free Throws Made: 1 point
    fantasyScore += player.freeThrows

    // Rebounds: 1.2 points
    fantasyScore += player.totalRebounds * 1.2f

    // Assists: 1.5 points
    fantasyScore += player.assists * 1.5f

    // Blocked Shots: 2 points
    fantasyScore += player.blocks * 2

    // Steals: 2 points
    fantasyScore += player.steals * 2

    // Turnovers: -1 points
    fantasyScore -= player.turnovers

    return "%.2f".format(fantasyScore)
}
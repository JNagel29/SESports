package com.example.jetpacktest.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.models.Player

@Composable
fun CompareResultsScreen(navigateBack: () -> Unit) {
    // Define player objects with hardcoded data
    val player1 = Player(
        name = "Lebron James",
        year = 2024,
        position = "Forward",
        team = "Lakers",
        points = 25.4f,
        assists = 8.1f,
        steals = 1.2f,
        blocks = 0.6f,
        totalRebounds = 7.2f,
        turnovers = 3.3f,
        personalFouls = 1.2f,
        minutesPlayed = 35.2f,
        fieldGoals = 9.5f,
        fieldGoalAttempts = 18.0f,
        fieldGoalPercent = 53.0f,
        threePointers = 2.2f,
        threePointerAttempts = 5.3f,
        threePointPercent = 40.6f,
        twoPointers = 7.4f,
        twoPointerAttempts = 12.7f,
        twoPointPercent = 58.1f,
        effectiveFieldGoalPercent = 59.0f,
        offensiveRebounds = 0.8f,
        defensiveRebounds = 6.3f
    )

    val player2 = Player(
        name = "Stephen Curry",
        year = 2024,
        position = "Guard",
        team = "Warriors",
        points = 26.8f,
        assists = 4.9f,
        steals = 0.8f,
        blocks = 0.3f,
        totalRebounds = 4.5f,
        turnovers = 2.9f,
        personalFouls = 1.6f,
        minutesPlayed = 32.7f,
        fieldGoals = 8.8f,
        fieldGoalAttempts = 19.7f,
        fieldGoalPercent = 44.8f,
        threePointers = 4.9f,
        threePointerAttempts = 12.1f,
        threePointPercent = 40.4f,
        twoPointers = 4.0f,
        twoPointerAttempts = 7.7f,
        twoPointPercent = 51.8f,
        effectiveFieldGoalPercent = 57.2f,
        offensiveRebounds = 0.5f,
        defensiveRebounds = 3.9f
    )

    // Display player information using com.example.jetpack test.screens.PlayerProfile
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ReturnToSearchHeader(navigateBack = navigateBack)
            Spacer(modifier = Modifier.height(15.dp))
            PlayerProfile(players = listOf(player1, player2))
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun PlayerProfile(players: List<Player>) {
    val player1 = players.getOrNull(0)
    val player2 = players.getOrNull(1)

    HorizontalDivider()
    player1?.let { player ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                player2?.let { otherPlayer ->
                    PlayerStats(player, otherPlayer)
                }
            }
        }
        HorizontalDivider()
    }
}

@Composable
fun PlayerStats(player: Player, otherPlayer: Player) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        StatRow("Name", player.name, otherPlayer.name)
        StatRow("Year", player.year.toString(), otherPlayer.year.toString())
        StatRow("Position", player.position, otherPlayer.position)
        StatRow("Team", player.team, otherPlayer.team)
        StatRow("Points", player.points.toString(), otherPlayer.points.toString())
        StatRow("Assists", player.assists.toString(), otherPlayer.assists.toString())
        StatRow("Steals", player.steals.toString(), otherPlayer.steals.toString())
        StatRow("Blocks", player.blocks.toString(), otherPlayer.blocks.toString())
        StatRow("Total Rebounds", player.totalRebounds.toString(), otherPlayer.totalRebounds.toString())
        StatRow("Turnovers", player.turnovers.toString(), otherPlayer.turnovers.toString())
        StatRow("Personal Fouls", player.personalFouls.toString(), otherPlayer.personalFouls.toString())
        StatRow("Minutes Played", player.minutesPlayed.toString(), otherPlayer.minutesPlayed.toString())
        StatRow("FG", player.fieldGoals.toString(), otherPlayer.fieldGoals.toString())
        StatRow("FGA", player.fieldGoalAttempts.toString(), otherPlayer.fieldGoalAttempts.toString())
        StatRow("FG%", player.fieldGoalPercent.toString(), otherPlayer.fieldGoalPercent.toString())
        StatRow("3P ", player.threePointers.toString(), otherPlayer.threePointers.toString())
        StatRow("3PA", player.threePointerAttempts.toString(), otherPlayer.threePointerAttempts.toString())
        StatRow("3P%", player.threePointPercent.toString(), otherPlayer.threePointPercent.toString())
        StatRow("2P", player.twoPointers.toString(), otherPlayer.twoPointers.toString())
        StatRow("2PA", player.twoPointerAttempts.toString(), otherPlayer.twoPointerAttempts.toString())
        StatRow("2P%", player.twoPointPercent.toString(), otherPlayer.twoPointPercent.toString())
        StatRow("EFG%", player.effectiveFieldGoalPercent.toString(), otherPlayer.effectiveFieldGoalPercent.toString())
        StatRow("ORB", player.offensiveRebounds.toString(), otherPlayer.offensiveRebounds.toString())
        StatRow("DRB", player.defensiveRebounds.toString(), otherPlayer.defensiveRebounds.toString())
    }
}
@Composable
fun StatRow(label: String, playerValue: String, otherPlayerValue: String) {
    val playerValueFloat = playerValue.toFloatOrNull()
    val otherPlayerValueFloat = otherPlayerValue.toFloatOrNull()

    val playerColor = when {
        label == "Turnovers" || label == "Personal Fouls" -> {
            if (playerValueFloat != null && otherPlayerValueFloat != null) {
                if (playerValueFloat < otherPlayerValueFloat) Color.Green else Color.Red
            } else Color.Black
        }
        playerValueFloat != null && otherPlayerValueFloat != null -> {
            if (playerValueFloat > otherPlayerValueFloat) Color.Green else Color.Red
        }
        else -> Color.Black
    }

    val otherPlayerColor = when {
        label == "Turnovers" || label == "Personal Fouls" -> {
            if (playerValueFloat != null && otherPlayerValueFloat != null) {
                if (otherPlayerValueFloat < playerValueFloat) Color.Green else Color.Red
            } else Color.Black
        }
        playerValueFloat != null && otherPlayerValueFloat != null -> {
            if (otherPlayerValueFloat > playerValueFloat) Color.Green else Color.Red
        }
        else -> Color.Black
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = playerValue,
            fontSize = if (label == "Name") 14.sp else 18.sp, // Adjust the font size for player names
            textAlign = TextAlign.End,
            color = if (label == "Year") Color.Black else playerColor,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = otherPlayerValue,
            fontSize = if (label == "Name") 14.sp else 18.sp, // Adjust the font size for player names
            textAlign = TextAlign.End,
            color = if (label == "Year") Color.Black else otherPlayerColor,
            modifier = Modifier.weight(1f)
        )
    }
}





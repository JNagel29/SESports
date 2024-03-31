package com.example.jetpacktest.screens

import ReturnToPreviousHeader
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
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.models.Player
import com.example.jetpacktest.ui.theme.CircularLoadingIcon
import com.example.jetpacktest.ui.theme.LargeDropdownMenu

@Composable
fun CompareResultsScreen(playerName1: String, playerName2: String, navigateBack: () -> Unit) {
    val databaseHandler = DatabaseHandler()
    var player1 by remember { mutableStateOf(Player()) }
    var player2 by remember { mutableStateOf(Player()) }
    var chosenYear1 by remember { mutableStateOf("") }
    var chosenYear2 by remember { mutableStateOf("") }
    var yearsList1 by remember { mutableStateOf<List<String>>(emptyList()) }
    var yearsList2 by remember { mutableStateOf<List<String>>(emptyList()) }
    var isFetching1 by remember { mutableStateOf(false)}
    var isFetching2 by remember { mutableStateOf(false)}



    LaunchedEffect(Unit) {
        isFetching1 = true
        //Fetch player 1 years and data
        databaseHandler.executeYears(playerName = playerName1) {result ->
            yearsList1 = result
            if (yearsList1.isNotEmpty()) {
                chosenYear1 = yearsList1.first() //Default to most recent year
                databaseHandler.executePlayerData(playerName1, chosenYear1) { data ->
                    player1 = data
                    isFetching1 = false
                }
            }
        }
        isFetching2 = true
        //Same for player2
        databaseHandler.executeYears(playerName = playerName2) {result ->
            yearsList2 = result
            if (yearsList2.isNotEmpty()) {
                chosenYear2 = yearsList2.first() //Default to most recent year
                databaseHandler.executePlayerData(playerName2, chosenYear2) { data ->
                    player2 = data
                    isFetching2 = false
                }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ReturnToPreviousHeader(navigateBack = navigateBack)
            Spacer(modifier = Modifier.height(15.dp))
            LargeDropdownMenu(
                label = "Select ${player1.name}'s Year:",
                items = yearsList1,
                selectedIndex = yearsList1.indexOf(chosenYear1),
                onItemSelected = { index, _ ->
                    val year = yearsList1[index]
                    //Check if newly selected stat is different from previous
                    if (year != chosenYear1) {
                        chosenYear1 = year
                        databaseHandler.executePlayerData(playerName1, chosenYear1) { data ->
                            player1 = data
                        }
                    }
                }
            )
            LargeDropdownMenu(
                label = "Select ${player2.name}'s Year:",
                items = yearsList2,
                selectedIndex = yearsList2.indexOf(chosenYear2),
                onItemSelected = { index, _ ->
                    val year = yearsList2[index]
                    //Check if newly selected stat is different from previous
                    if (year != chosenYear2) {
                        chosenYear2 = year
                        databaseHandler.executePlayerData(playerName2, chosenYear2) { data ->
                            player2 = data
                        }
                    }
                }
            )
            if (!isFetching1 && !isFetching2) PlayerProfile(players = listOf(player1, player2))
            else CircularLoadingIcon()
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
    }
}

@Composable
fun PlayerStats(player1: Player, player2: Player) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        StatRow("Name", player1.name, player2.name)
        StatRow("Year", player1.year.toString(), player2.year.toString())
        StatRow("Position", player1.position, player2.position)
        StatRow("Team", player1.team, player2.team)
        StatRow("Points", player1.points.toString(), player2.points.toString())
        StatRow("Assists", player1.assists.toString(), player2.assists.toString())
        StatRow("Steals", player1.steals.toString(), player2.steals.toString())
        StatRow("Blocks", player1.blocks.toString(), player2.blocks.toString())
        StatRow("Total Rebounds", player1.totalRebounds.toString(), player2.totalRebounds.toString())
        StatRow("Turnovers", player1.turnovers.toString(), player2.turnovers.toString())
        StatRow("Personal Fouls", player1.personalFouls.toString(), player2.personalFouls.toString())
        StatRow("Minutes Played", player1.minutesPlayed.toString(), player2.minutesPlayed.toString())
        StatRow("FG", player1.fieldGoals.toString(), player2.fieldGoals.toString())
        StatRow("FGA", player1.fieldGoalAttempts.toString(), player2.fieldGoalAttempts.toString())
        StatRow("FG%", player1.fieldGoalPercent.toString(), player2.fieldGoalPercent.toString())
        StatRow("3P ", player1.threePointers.toString(), player2.threePointers.toString())
        StatRow("3PA", player1.threePointerAttempts.toString(), player2.threePointerAttempts.toString())
        StatRow("3P%", player1.threePointPercent.toString(), player2.threePointPercent.toString())
        StatRow("2P", player1.twoPointers.toString(), player2.twoPointers.toString())
        StatRow("2PA", player1.twoPointerAttempts.toString(), player2.twoPointerAttempts.toString())
        StatRow("2P%", player1.twoPointPercent.toString(), player2.twoPointPercent.toString())
        StatRow("EFG%", player1.effectiveFieldGoalPercent.toString(), player2.effectiveFieldGoalPercent.toString())
        StatRow("ORB", player1.offensiveRebounds.toString(), player2.offensiveRebounds.toString())
        StatRow("DRB", player1.defensiveRebounds.toString(), player2.defensiveRebounds.toString())
    }
}
@Composable
fun StatRow(label: String, player1Value: String, player2Value: String) {
    val playerValueFloat = player1Value.toFloatOrNull()
    val player2ValueFloat = player2Value.toFloatOrNull()

    val player1Color = when {
        //Lower is better
        label == "Turnovers" || label == "Personal Fouls" -> {
            if (playerValueFloat != null && player2ValueFloat != null) {
                if (playerValueFloat < player2ValueFloat) Color.Green else Color.Red
            } else Color.Black
        }
        //Higher is better
        playerValueFloat != null && player2ValueFloat != null -> {
            if (playerValueFloat > player2ValueFloat) Color.Green else Color.Red
        }
        else -> Color.Black //For non-stat values
    }

    val player2Color = when {
        label == "Turnovers" || label == "Personal Fouls" -> {
            if (playerValueFloat != null && player2ValueFloat != null) {
                if (player2ValueFloat < playerValueFloat) Color.Green else Color.Red
            } else Color.Black
        }
        playerValueFloat != null && player2ValueFloat != null -> {
            if (player2ValueFloat > playerValueFloat) Color.Green else Color.Red
        }
        else -> Color.Black
    }

    Column {
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
                text = player1Value,
                fontSize = if (label == "Name") 14.sp else 18.sp,
                textAlign = TextAlign.End,
                color = if (label == "Year") Color.Black else player1Color,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = player2Value,
                fontSize = if (label == "Name") 14.sp else 18.sp,
                textAlign = TextAlign.End,
                color = if (label == "Year") Color.Black else player2Color,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
    }
}


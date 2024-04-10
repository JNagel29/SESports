package com.example.jetpacktest.screens

import ReturnToPreviousHeader
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.R
import com.example.jetpacktest.models.NbaTeam
import com.example.jetpacktest.models.Player
import com.example.jetpacktest.ui.components.CircularLoadingIcon
import com.example.jetpacktest.ui.components.LargeDropdownMenu

@Composable
fun CompareResultsScreen(playerName1: String, playerName2: String,navigateBack: () -> Unit,
                         ) {
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
            if (isFetching1 || isFetching2) {
                CircularLoadingIcon()
            }
            else {
                Row(modifier = Modifier.fillMaxWidth()) {
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
                        },
                        modifier = Modifier.weight(1f)
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
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                PlayerProfile(players = listOf(player1, player2))
            }
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
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                Box(modifier = Modifier.padding(end = 16.dp)) {
                    PlayerCard(player, player2!!)
                }
                Box(modifier = Modifier.padding(end = 16.dp)) {
                    PlayerCard(player2!!, player1)
                }
            }
        }
    }
}





@Composable
fun PlayerStats(player1: Player, player2: Player) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PlayerCard(player1, player2)
        PlayerCard(player2, player1)
    }
}

@Composable
fun PlayerCard(player: Player, opponent: Player) {
    val team = player.team
    Box(
        modifier = Modifier
            .width(200.dp)
            .shadow(elevation = 4.dp)
            .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp)) // Border with rounded corners
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFF5F5DC)) // Beige background color
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = player.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatRow("Year", player.year.toString(), false)
                StatRow("Position", player.position, false)
                StatRow("Team", player.team, false)
                StatRow("Points", player.points.toString(), player.points > opponent.points)
                StatRow("Assists", player.assists.toString(), player.assists > opponent.assists)
                StatRow("Steals", player.steals.toString(), player.steals > opponent.steals)
                StatRow("Blocks", player.blocks.toString(), player.blocks > opponent.blocks)
                StatRow("Total Rebounds", player.totalRebounds.toString(), player.totalRebounds > opponent.totalRebounds)
                StatRow("Turnovers", player.turnovers.toString(), player.turnovers < opponent.turnovers)
                StatRow("Personal Fouls", player.personalFouls.toString(), player.personalFouls < opponent.personalFouls)
                StatRow("Minutes Played", player.minutesPlayed.toString(), player.minutesPlayed > opponent.minutesPlayed)
                StatRow("FG", player.fieldGoals.toString(), player.fieldGoals > opponent.fieldGoals)
                StatRow("FGA", player.fieldGoalAttempts.toString(), player.fieldGoalAttempts > opponent.fieldGoalAttempts)
                StatRow("FG%", player.fieldGoalPercent.toString(), player.fieldGoalPercent > opponent.fieldGoalPercent)
                StatRow("3P ", player.threePointers.toString(), player.threePointers > opponent.threePointers)
                StatRow("3PA", player.threePointerAttempts.toString(), player.threePointerAttempts > opponent.threePointerAttempts)
                StatRow("3P%", player.threePointPercent.toString(), player.threePointPercent > opponent.threePointPercent)
                StatRow("2P", player.twoPointers.toString(), player.twoPointers > opponent.twoPointers)
                StatRow("2PA", player.twoPointerAttempts.toString(), player.twoPointerAttempts > opponent.twoPointerAttempts)
                StatRow("2P%", player.twoPointPercent.toString(), player.twoPointPercent > opponent.twoPointPercent)
                StatRow("EFG%", player.effectiveFieldGoalPercent.toString(), player.effectiveFieldGoalPercent > opponent.effectiveFieldGoalPercent)
                StatRow("ORB", player.offensiveRebounds.toString(), player.offensiveRebounds > opponent.offensiveRebounds)
                StatRow("DRB", player.defensiveRebounds.toString(), player.defensiveRebounds > opponent.defensiveRebounds)
            }
        }
    }
}


@Composable
fun StatRow(label: String, value: String, playerHasHigherStat: Boolean) {
    val statColor = if (playerHasHigherStat) Color(0xFF46923c) else Color(0xFFF5F5DC)
    val borderColor = Color(0xFFF5F5DC)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .background(statColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                textAlign = TextAlign.End,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}



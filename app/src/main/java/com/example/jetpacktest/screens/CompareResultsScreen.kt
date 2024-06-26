package com.example.jetpacktest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.HeadshotHandler
import com.example.jetpacktest.R
import com.example.jetpacktest.getFantasyRating
import com.example.jetpacktest.models.Player
import com.example.jetpacktest.ui.components.CircularLoadingIcon
import com.example.jetpacktest.ui.components.ExpandableCategory
import com.example.jetpacktest.ui.components.LargeDropdownMenu
import com.example.jetpacktest.ui.components.ReturnToPreviousHeader

@Composable
fun CompareResultsScreen(playerName1: String, playerName2: String, navigateBack: () -> Unit) {
    val databaseHandler = DatabaseHandler()
    val headshotHandler = HeadshotHandler()
    var player1 by rememberSaveable { mutableStateOf(Player()) }
    var player2 by rememberSaveable { mutableStateOf(Player()) }
    var chosenYear1 by rememberSaveable { mutableStateOf("") }
    var chosenYear2 by rememberSaveable { mutableStateOf("") }
    var yearsList1 by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var yearsList2 by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var imgId1 by rememberSaveable { mutableIntStateOf(-1) }
    var imgId2 by rememberSaveable { mutableIntStateOf(-1) }
    var isFetching1 by remember { mutableStateOf(false)}
    var isFetching2 by remember { mutableStateOf(false)}
    var isFetchingImage1 by remember { mutableStateOf(false)}
    var isFetchingImage2 by remember { mutableStateOf(false)}

    LaunchedEffect(Unit) {
        if (yearsList1.isEmpty()) {
            isFetching1 = true
            databaseHandler.executeYears(playerName = playerName1) { result ->
                yearsList1 = result
                if (yearsList1.isNotEmpty()) {
                    chosenYear1 = yearsList1.first() //Default to most recent year
                    databaseHandler.executePlayerData(playerName1, chosenYear1) { data ->
                        player1 = data
                        isFetching1 = false
                    }
                }
            }
        }
        if (yearsList2.isEmpty()) {
            isFetching2 = true
            databaseHandler.executeYears(playerName = playerName2) { result ->
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
        if (imgId1 == -1 || imgId2 == -1) {
            isFetchingImage1 = true
            headshotHandler.fetchImageId(playerName = playerName1) { result ->
                imgId1 = result
                isFetchingImage1 = false
            }
            isFetchingImage2 = true
            headshotHandler.fetchImageId(playerName = playerName2) { result ->
                imgId2 = result
                isFetchingImage2 = false
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            ReturnToPreviousHeader(navigateBack = navigateBack, label = "Compare")
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
                Spacer(modifier = Modifier.height(5.dp))
                NamesAndHeadshots(
                    player1Name = player1.name,
                    player2Name = player2.name,
                    imgId1 = imgId1,
                    imgId2 = imgId2,
                    headshotHandler = headshotHandler
                )
                PlayerComparison(player1 = player1, player2 = player2)
            }
        }
    }
}
@Composable
fun PlayerComparison(player1: Player, player2: Player) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            StatCategory(
                title = "Main Stats",
                stats = getPlayerStats(player1, player2, category = "Main Stats"),
                player1 = player1,
                player2 = player2,
                showByDefault = true
            )
        }
        item {
            StatCategory(
                title = "Shooting Splits",
                stats = getPlayerStats(player1, player2, category = "Shooting Splits"),
                player1 = player1,
                player2 = player2,
                showByDefault = false
            )
        }
        item {
            StatCategory(
                title = "Defensive Efficiency and Usage",
                stats = getPlayerStats(player1, player2, category = "Defensive Efficiency and Usage"),
                player1 = player1,
                player2 = player2,
                showByDefault = false
            )
        }
    }
}

@Composable
fun StatCategory(
    title: String,
    stats: List<Pair<String, Pair<Any, Any>>>,
    player1: Player,
    player2: Player,
    showByDefault: Boolean
) {
    ExpandableCategory(title = title, showByDefault = showByDefault) {
        Column(modifier = Modifier.padding(4.dp)) {
            stats.forEachIndexed { index, (statName, statValues) ->
                ExpandableStatRow(
                    statName = statName,
                    statValues = statValues,
                    player1 = player1,
                    player2 = player2
                )
                if (index < stats.lastIndex) {
                    HorizontalDivider(color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ExpandableStatRow(
    statName: String,
    statValues: Pair<Any, Any>,
    player1: Player,
    player2: Player
) {
    val (player1Value, player2Value) = statValues
    if (statName != "Team") {
        StatRow(
            label = statName,
            player1Value = player1Value.toString(),
            player2Value = player2Value.toString()
        )
    } else {
        StatRow(
            label = statName,
            player1Value = player1.team,
            player2Value = player2.team
        )
    }
}

@Composable
fun NamesAndHeadshots(
    player1Name: String,
    player2Name: String,
    imgId1: Int,
    imgId2: Int,
    headshotHandler: HeadshotHandler
) {
    val player1HeadshotUrl = if (imgId1 == -1 || imgId1 == 0) "DEFAULT"
    else "https://cdn.nba.com/headshots/nba/latest/1040x760/$imgId1.png"
    val player2HeadshotUrl = if (imgId2 == -1 || imgId2 == 0) "DEFAULT"
    else "https://cdn.nba.com/headshots/nba/latest/1040x760/$imgId2.png"
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
    ) {
        Text(
            text = player1Name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.weight(1f),
        )
        headshotHandler.ComposeImage(
            imgToCompose = player1HeadshotUrl,
            contentDesc = player1Name,
            width = 90.dp,
            height = 90.dp,
            modifier = Modifier
                .border(2.dp, Color.Black, CircleShape)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
        )
        Spacer(modifier = Modifier.width(5.dp))
        headshotHandler.ComposeImage(
            imgToCompose = player2HeadshotUrl,
            contentDesc = player2Name,
            width = 90.dp,
            height = 90.dp,
            modifier = Modifier
                .border(2.dp, Color.Black, CircleShape)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))

        )
        Text(
            text = player2Name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun StatRow(label: String, player1Value: String, player2Value: String) {
    val player1HasHigherStat = checkIfPlayer1HasHigherStat(
        statName = label,
        player1Value = player1Value,
        player2Value = player2Value
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = player1Value,
            fontSize = 20.sp,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .wrapContentSize()
                .background(
                    if (player1HasHigherStat == true) colorResource(R.color.higherStat)
                    else Color.Transparent
                )
                .weight(1.5f)
                .padding(horizontal = 5.dp)
        )
        Text(
            text = label,
            fontSize = 16.sp,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(2f)
                .padding(horizontal = 5.dp)
        )
        Text(
            text = player2Value,
            fontSize = 20.sp,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.End,
            modifier = Modifier
                .wrapContentSize()
                .background(
                    if (player1HasHigherStat == false) colorResource(R.color.higherStat)
                    else Color.Transparent
                )
                .weight(1.5f)
                .padding(horizontal = 5.dp)
        )
    }
}

private fun getPlayerStats(
    player1: Player,
    player2: Player,
    category: String
): List<Pair<String, Pair<Any, Any>>> {


    when (category) {
        "Main Stats" -> return listOf(
            "Team" to Pair(player1.team, player2.team),
            "Points" to Pair(player1.points, player2.points),
            "Assists" to Pair(player1.assists, player2.assists),
            "Total Rebounds" to Pair(player1.totalRebounds, player2.totalRebounds),
            "Off. Rebounds" to Pair(player1.offensiveRebounds, player2.offensiveRebounds),
            "Def. Rebounds" to Pair(player1.defensiveRebounds, player2.defensiveRebounds),
            "Fantasy Rating" to Pair(getFantasyRating(player1), getFantasyRating(player2)))
        "Shooting Splits" -> return listOf(
            "Field Goals" to Pair(player1.fieldGoals, player2.fieldGoals),
            "Field Goal Attempts" to Pair(player1.fieldGoalAttempts, player2.fieldGoalAttempts),
            "Field Goal %" to Pair(player1.fieldGoalPercent, player2.fieldGoalPercent),
            "3 Pointers" to Pair(player1.threePointers, player2.threePointers),
            "3 Point Attempts" to Pair(player1.threePointerAttempts, player2.threePointerAttempts),
            "3 Point %" to Pair(player1.threePointPercent, player2.threePointPercent),
            "2 Pointers" to Pair(player1.twoPointers, player2.twoPointers),
            "2 Point Attempts" to Pair(player1.twoPointerAttempts, player2.twoPointerAttempts),
            "2 Point %" to Pair(player1.twoPointPercent, player2.twoPointPercent),
            "Free Throws" to Pair(player1.freeThrows, player2.freeThrows),
            "Free Throw Attempts" to Pair(player1.freeThrowAttempts, player2.freeThrowAttempts),
            "Free Throw %" to Pair(player1.freeThrowPercent, player2.freeThrowPercent),
            "Effective Field Goal %" to Pair(
                player1.effectiveFieldGoalPercent,
                player2.effectiveFieldGoalPercent
            ))
        "Defensive Efficiency and Usage" -> return listOf(
            "Steals" to Pair(player1.steals, player2.steals),
            "Blocks" to Pair(player1.blocks, player2.blocks),
            "Fouls" to Pair(player1.personalFouls, player2.personalFouls),
            "Minutes Played" to Pair(player1.minutesPlayed, player2.minutesPlayed),
            "Games Started" to Pair(player1.gamesStarted, player2.gamesStarted),
        )
        else -> return listOf("N/A" to Pair("N/A", "N/A"))
    }
}

private fun checkIfPlayer1HasHigherStat(
    statName: String,
    player1Value: String,
    player2Value: String
): Boolean? {
    val player1Val = player1Value.toFloatOrNull()
    val player2Val = player2Value.toFloatOrNull()

    if (player1Val == null|| player2Val == null || player1Val == player2Val) return null
    return if (statName == "Turnovers" || statName == "Personal Fouls") player1Val < player2Val
    else player1Val > player2Val
}



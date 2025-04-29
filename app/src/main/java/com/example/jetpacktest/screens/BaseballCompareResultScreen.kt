package com.example.jetpacktest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.jetpacktest.models.Pitcher
import com.example.jetpacktest.models.Batter
import com.example.jetpacktest.ui.components.ExpandableCategory
import com.example.jetpacktest.ui.components.LargeDropdownMenu
import com.example.jetpacktest.ui.components.ReturnToPreviousHeader

// Data class to represent players
data class BaseballComparePlayer(
    val playerName: String,
    val isPitcher: Boolean
)

@Composable
fun BaseballCompareResultScreen(
    player1: BaseballComparePlayer,
    player2: BaseballComparePlayer,
    navigateBack: () -> Unit
) {
    val databaseHandler = DatabaseHandler()
    val yearOptions = listOf("2019", "2020", "2021", "2022", "2023", "2024")

    var selectedYear1 by remember { mutableStateOf("2024") }
    var selectedYear2 by remember { mutableStateOf("2024") }

    var pitcher1 by remember { mutableStateOf<Pitcher?>(null) }
    var pitcher2 by remember { mutableStateOf<Pitcher?>(null) }
    var batter1 by remember { mutableStateOf<Batter?>(null) }
    var batter2 by remember { mutableStateOf<Batter?>(null) }

    LaunchedEffect(selectedYear1, selectedYear2) {
        if (player1.isPitcher) databaseHandler.executePitcherData(player1.playerName, selectedYear1) { pitcher1 = it }
        else databaseHandler.getBatterData(player1.playerName, selectedYear1) { batter1 = it }

        if (player2.isPitcher) databaseHandler.executePitcherData(player2.playerName, selectedYear2) { pitcher2 = it }
        else databaseHandler.getBatterData(player2.playerName, selectedYear2) { batter2 = it }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            ReturnToPreviousHeader(navigateBack = navigateBack, label = "Compare Players")
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                LargeDropdownMenu(
                    label = "${player1.playerName} Year",
                    items = yearOptions,
                    selectedIndex = yearOptions.indexOf(selectedYear1),
                    onItemSelected = { index, _ -> selectedYear1 = yearOptions[index] },
                    modifier = Modifier.weight(1f)
                )
                LargeDropdownMenu(
                    label = "${player2.playerName} Year",
                    items = yearOptions,
                    selectedIndex = yearOptions.indexOf(selectedYear2),
                    onItemSelected = { index, _ -> selectedYear2 = yearOptions[index] },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = player1.playerName,
                            fontSize = 20.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "vs",
                            fontSize = 18.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(0.5f)
                        )
                        Text(
                            text = player2.playerName,
                            fontSize = 20.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    if (player1.isPitcher && player2.isPitcher) {
                        ExpandableCategory(title = "Main Stats", showByDefault = true) {
                            Column {
                                CompareStatRow("ERA", pitcher1?.era, pitcher2?.era)
                                CompareStatRow("WHIP", pitcher1?.whip, pitcher2?.whip)
                                CompareStatRow("Wins", pitcher1?.wins, pitcher2?.wins)
                                CompareStatRow("Losses", pitcher1?.losses, pitcher2?.losses)
                                CompareStatRow("Saves", pitcher1?.saves, pitcher2?.saves)
                                CompareStatRow("Innings Pitched", pitcher1?.inningsPitched, pitcher2?.inningsPitched)
                            }
                        }
                        ExpandableCategory(title = "Pitching Details", showByDefault = false) {
                            Column {
                                CompareStatRow("Strikeouts", pitcher1?.strikeOuts, pitcher2?.strikeOuts)
                                CompareStatRow("Strikeouts Per 9 Inn", pitcher1?.strikeoutsPer9Inn, pitcher2?.strikeoutsPer9Inn)
                                CompareStatRow("Strike Percentage", pitcher1?.strikePercentage, pitcher2?.strikePercentage)
                            }
                        }
                        ExpandableCategory(title = "Game & Situational", showByDefault = false) {
                            Column {
                                CompareStatRow("Games Played", pitcher1?.gamesPlayed, pitcher2?.gamesPlayed)
                                CompareStatRow("Games Started", pitcher1?.gamesStarted, pitcher2?.gamesStarted)
                                CompareStatRow("Games Finished", pitcher1?.gamesFinished, pitcher2?.gamesFinished)
                                CompareStatRow("Complete Games", pitcher1?.completeGames, pitcher2?.completeGames)
                                CompareStatRow("Shutouts", pitcher1?.shutouts, pitcher2?.shutouts)
                                CompareStatRow("Save Opportunities", pitcher1?.saveOpportunities, pitcher2?.saveOpportunities)
                                CompareStatRow("Pickoffs", pitcher1?.pickoffs, pitcher2?.pickoffs)
                                CompareStatRow("Wild Pitches", pitcher1?.wildPitches, pitcher2?.wildPitches)
                                CompareStatRow("Ground Outs", pitcher1?.groundOuts, pitcher2?.groundOuts)
                                CompareStatRow("Air Outs", pitcher1?.airOuts, pitcher2?.airOuts)
                                CompareStatRow("Hit Batsmen", pitcher1?.hitBatsmen, pitcher2?.hitBatsmen)
                                CompareStatRow("Ground Into Double Play", pitcher1?.groundIntoDoublePlay, pitcher2?.groundIntoDoublePlay)
                            }
                        }
                        ExpandableCategory(title = "Efficiency Splits", showByDefault = false) {
                            Column {
                                CompareStatRow("Hits Per 9 Innings", pitcher1?.hitsPer9Inn, pitcher2?.hitsPer9Inn)
                                CompareStatRow("Walks Per 9 Inn", pitcher1?.walksPer9Inn, pitcher2?.walksPer9Inn)
                                CompareStatRow("Home Runs Per 9", pitcher1?.homeRunsPer9, pitcher2?.homeRunsPer9)
                                CompareStatRow("Runs Scored Per 9", pitcher1?.runsScoredPer9, pitcher2?.runsScoredPer9)
                                CompareStatRow("SLG", pitcher1?.slg, pitcher2?.slg)
                            }
                        }
                    } else {
                        ExpandableCategory(title = "Main Stats", showByDefault = true) {
                            Column {
                                CompareStatRow("Batting Average (AVG)", batter1?.avg, batter2?.avg)
                                CompareStatRow("On Base Percentage (OBP)", batter1?.obp, batter2?.obp)
                                CompareStatRow("On-Base Plus Slugging (OPS)", batter1?.ops, batter2?.ops)
                                CompareStatRow("Hits", batter1?.hits, batter2?.hits)
                                CompareStatRow("Home Runs", batter1?.homeRuns, batter2?.homeRuns)
                                CompareStatRow("Runs Batted In (RBI)", batter1?.rbi, batter2?.rbi)
                                CompareStatRow("Runs", batter1?.runs, batter2?.runs)
                            }
                        }
                        ExpandableCategory(title = "Batting Details", showByDefault = false) {
                            Column {
                                CompareStatRow("At Bats", batter1?.atBats, batter2?.atBats)
                                CompareStatRow("Plate Appearances", batter1?.plateAppearances, batter2?.plateAppearances)
                                CompareStatRow("Doubles", batter1?.doubles, batter2?.doubles)
                                CompareStatRow("Triples", batter1?.triples, batter2?.triples)
                                CompareStatRow("Stolen Bases", batter1?.stolenBases, batter2?.stolenBases)
                            }
                        }
                        ExpandableCategory(title = "Game & Situational", showByDefault = false) {
                            Column {
                                CompareStatRow("Games Played", batter1?.gamesPlayed, batter2?.gamesPlayed)
                                CompareStatRow("Ground Outs", batter1?.groundOuts, batter2?.groundOuts)
                                CompareStatRow("Ground Into Double Play", batter1?.groundIntoDoublePlay, batter2?.groundIntoDoublePlay)
                                CompareStatRow("Hit By Pitch", batter1?.hitByPitch, batter2?.hitByPitch)
                                CompareStatRow("Left on Base", batter1?.leftOnBase, batter2?.leftOnBase)
                                CompareStatRow("Sac Bunts", batter1?.sacBunts, batter2?.sacBunts)
                                CompareStatRow("Sac Flies", batter1?.sacFlies, batter2?.sacFlies)
                            }
                        }
                        ExpandableCategory(title = "Efficiency Splits", showByDefault = false) {
                            Column {
                                CompareStatRow("BABIP", batter1?.babip, batter2?.babip)
                                CompareStatRow("Slugging Percentage (SLG)", batter1?.slg, batter2?.slg)
                                CompareStatRow("Stolen Base Percentage", batter1?.stolenBasePercentage, batter2?.stolenBasePercentage)
                                CompareStatRow("At Bats per HR", batter1?.atBatsPerHomeRun, batter2?.atBatsPerHomeRun)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompareStatRow(
    label: String,
    player1Value: Any?,
    player2Value: Any?,
    bold: Boolean = false
) {
    val player1Numeric = player1Value.toString().toFloatOrNull()
    val player2Numeric = player2Value.toString().toFloatOrNull()

    val player1Better = if (player1Numeric != null && player2Numeric != null) {
        if (isLowerBetter(label)) player1Numeric < player2Numeric else player1Numeric > player2Numeric
    } else null

    val player2Better = if (player1Numeric != null && player2Numeric != null) {
        if (isLowerBetter(label)) player2Numeric < player1Numeric else player2Numeric > player1Numeric
    } else null

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = player1Value?.toString() ?: "-",
            fontSize = 18.sp,
            modifier = Modifier
                .weight(1f)
                .background(if (player1Better == true) Color.Green else Color.Transparent)
                .padding(4.dp),
            textAlign = TextAlign.Start,
            fontWeight = if (bold) FontWeight.Bold else null
        )
        Text(
            text = label,
            fontSize = 16.sp,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.Center,
            fontWeight = if (bold) FontWeight.Bold else null
        )
        Text(
            text = player2Value?.toString() ?: "-",
            fontSize = 18.sp,
            modifier = Modifier
                .weight(1f)
                .background(if (player2Better == true) Color.Green else Color.Transparent)
                .padding(4.dp),
            textAlign = TextAlign.End,
            fontWeight = if (bold) FontWeight.Bold else null
        )
    }
}

private fun isLowerBetter(statName: String): Boolean {
    return statName.lowercase() in listOf(
        "era", "losses", "walks per 9 inn", "runs scored per 9", "whip",
        "ground outs", "grounds into double play", "left on base", "at bats per hr", "wild pitches", "hit batsmen", "home runs per 9", "runs scored per 9", "hits per 9 innings"
    )
}

package com.example.jetpacktest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.R
import com.example.jetpacktest.models.Batter
import com.example.jetpacktest.models.Pitcher
import com.example.jetpacktest.models.TeamMaps
import com.example.jetpacktest.ui.components.CircularLoadingIcon
import com.example.jetpacktest.ui.components.ExpandableCategory
import com.example.jetpacktest.ui.components.LargeDropdownMenu
import com.example.jetpacktest.ui.components.ReturnToPreviousHeader

@Composable
fun BaseballProfileScreen(
    playerName: String,
    isPitcher: Boolean,
    navigateBack: () -> Unit,
    showSnackBar: (String) -> Unit
) {
    val databaseHandler = DatabaseHandler()
    var batterStats by remember { mutableStateOf<Batter?>(null) }
    var pitcherStats by remember { mutableStateOf<Pitcher?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val defaultYear = "2024"
    var yearsList by remember { mutableStateOf<List<String>>(emptyList()) }
    var chosenYear by remember { mutableStateOf(defaultYear) }

    LaunchedEffect(playerName, isPitcher) {
        if (isPitcher) {
            databaseHandler.executePitcherData(playerName, defaultYear) {
                pitcherStats = it
                isLoading = false
            }
            databaseHandler.executePitcherYears(playerName) { result ->
                yearsList = result
            }
        } else {
            databaseHandler.getBatterData(playerName, defaultYear) {
                batterStats = it
                isLoading = false
            }
            databaseHandler.executeBatterYears(playerName) { result ->
                yearsList = result
            }
        }
    }


    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            ReturnToPreviousHeader(navigateBack = navigateBack, label = "Back")
            Spacer(modifier = Modifier.height(15.dp))

            if (isLoading) {
                CircularLoadingIcon()
            } else {

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        if (isPitcher && pitcherStats != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colorResource(TeamMaps.baseballTeamColorsMap[pitcherStats!!.team] ?: R.color.light_red))
                            ) {
                                NameAndTeamInfo(
                                    name = pitcherStats!!.playerName,
                                    team = pitcherStats!!.team,
                                    position = pitcherStats!!.position
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                MainStatBarPitcher(pitcherStats!!)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (yearsList.isNotEmpty()) {
                                LargeDropdownMenu(
                                    label = "Select Year",
                                    items = yearsList,
                                    selectedIndex = yearsList.indexOf(chosenYear),
                                    onItemSelected = { index, _ ->
                                        chosenYear = yearsList[index]
                                        isLoading = true
                                        databaseHandler.executePitcherData(playerName, chosenYear) {
                                            pitcherStats = it
                                            isLoading = false
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            PitcherStatsSection(pitcherStats!!)
                        } else if (!isPitcher && batterStats != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colorResource(TeamMaps.baseballTeamColorsMap[batterStats!!.team] ?: R.color.light_red))
                            ) {
                                NameAndTeamInfo(
                                    name = batterStats!!.playerName ?: "",
                                    team = batterStats!!.team ?: "",
                                    position = batterStats!!.position ?: ""
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                MainStatBarBatter(batterStats!!)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (yearsList.isNotEmpty()) {
                                LargeDropdownMenu(
                                    label = "Select Year",
                                    items = yearsList,
                                    selectedIndex = yearsList.indexOf(chosenYear),
                                    onItemSelected = { index, _ ->
                                        chosenYear = yearsList[index]
                                        isLoading = true
                                        databaseHandler.getBatterData(playerName, chosenYear) {
                                            batterStats = it
                                            isLoading = false
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            BatterStatsSection(batterStats!!)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun MainStatBarBatter(stats: Batter) {
    val backgroundColor = colorResource(
        id = TeamMaps.baseballTeamColorsMap[stats.team] ?: R.color.light_red
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatBox(label = "HR", value = stats.homeRuns.toString())
        VerticalDivider(modifier = Modifier.height(50.dp), color = Color.White)
        StatBox(label = "RBI", value = stats.rbi.toString())
        VerticalDivider(modifier = Modifier.height(50.dp), color = Color.White)
        StatBox(label = "AVG", value = String.format("%.3f", stats.avg))
    }
}

@Composable
fun MainStatBarPitcher(stats: Pitcher) {
    val backgroundColor = colorResource(
        id = TeamMaps.baseballTeamColorsMap[stats.team] ?: R.color.light_red
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatBox(label = "ERA", value = String.format("%.2f", stats.era))
        VerticalDivider(modifier = Modifier.height(50.dp), color = Color.White)
        StatBox(label = "WHIP", value = String.format("%.2f", stats.whip))
        VerticalDivider(modifier = Modifier.height(50.dp), color = Color.White)
        StatBox(label = "W-L", value = "${stats.wins}-${stats.losses}")
    }
}

@Composable
fun NameAndTeamInfo(name: String, team: String, position: String) {
    val backgroundColor = colorResource(
        id = TeamMaps.baseballTeamColorsMap[team] ?: R.color.light_red
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "$team | $position",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun PitcherStatsSection(stats: Pitcher) {
    ExpandableCategory(title = "Main Stats", showByDefault = true) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            StatRow("ERA", stats.era)
            StatRow("WHIP", stats.whip)
            StatRow("Wins", stats.wins)
            StatRow("Losses", stats.losses)
            StatRow("Saves", stats.saves)
            StatRow("Innings Pitched", stats.inningsPitched)
        }
    }
    ExpandableCategory(title = "Pitching Details", showByDefault = false) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            StatRow("Strikeouts", stats.strikeOuts)
            StatRow("Strikeouts Per 9 Innings", stats.strikeoutsPer9Inn)
            StatRow("Strike Percentage", stats.strikePercentage)
        }
    }
    ExpandableCategory(title = "Game & Situational", showByDefault = false) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            StatRow("Games Played", stats.gamesPlayed)
            StatRow("Games Started", stats.gamesStarted)
            StatRow("Games Finished", stats.gamesFinished)
            StatRow("Complete Games", stats.completeGames)
            StatRow("Shutouts", stats.shutouts)
            StatRow("Pickoffs", stats.pickoffs)
            StatRow("Wild Pitches", stats.wildPitches)
        }
    }
}

@Composable
fun BatterStatsSection(stats: Batter) {
    ExpandableCategory(title = "Main Stats", showByDefault = true) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            StatRow("Batting Average (AVG)", stats.avg)
            StatRow("On Base Percentage (OBP)", stats.obp)
            StatRow("On-Base Plus Slugging (OPS)", stats.ops)
            StatRow("Hits", stats.hits)
            StatRow("Home Runs", stats.homeRuns)
            StatRow("Runs Batted In (RBI)", stats.rbi)
        }
    }
    ExpandableCategory(title = "Batting Details", showByDefault = false) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            StatRow("At Bats", stats.atBats)
            StatRow("Plate Appearances", stats.plateAppearances)
            StatRow("Doubles", stats.doubles)
            StatRow("Triples", stats.triples)
            StatRow("Stolen Bases", stats.stolenBases)
        }
    }
    ExpandableCategory(title = "Game & Situational", showByDefault = false) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            StatRow("Games Played", stats.gamesPlayed)
            StatRow("Ground Outs", stats.groundOuts)
            StatRow("Ground Into Double Play", stats.groundIntoDoublePlay)
            StatRow("Hit By Pitch", stats.hitByPitch)
            StatRow("Left On Base", stats.leftOnBase)
        }
    }
}

@Composable
fun StatRow(label: String, value: Any?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.Start
        )
        Text(
            text = value?.toString() ?: "-",
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Medium
        )
    }
    Spacer(modifier = Modifier.height(6.dp))
}

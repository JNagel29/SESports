package com.example.jetpacktest.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.dropZeroBeforeDecimal
import com.example.jetpacktest.models.TeamStanding
import com.example.jetpacktest.ui.components.LargeDropdownMenu
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StandingsScreen(
    westernFlow: Flow<List<TeamStanding>>,
    easternFlow: Flow<List<TeamStanding>>,
    navigateToTeamProfile: (String) -> Unit,
    updateStandingsByYear: (String) -> Unit,
    yearOptions: List<String>
) {
    val westernStandings by westernFlow.collectAsState(initial = emptyList())
    val easternStandings by easternFlow.collectAsState(initial = emptyList())
    var chosenYear by rememberSaveable { mutableStateOf(yearOptions.first()) }

    val combinedStandings = easternStandings + westernStandings
    LazyColumn {
        item {
            LargeDropdownMenu(
                label = "Select Year:",
                items = yearOptions,
                selectedIndex = yearOptions.indexOf(chosenYear),
                onItemSelected = { index, _ ->
                    val year = yearOptions[index]
                    if (year != chosenYear) {
                        chosenYear = year
                        updateStandingsByYear(chosenYear)
                    }
                },
                modifier = Modifier
                    .padding(5.dp)
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            )
    navigateToBrackets: () -> Unit
) {
    val westernStandings by westernFlow.collectAsState(initial = emptyList())
    val easternStandings by easternFlow.collectAsState(initial = emptyList())

    val combinedStandings = easternStandings + westernStandings

    LazyColumn {
        stickyHeader {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Standings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    fontFamily = FontFamily.Serif,
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        navigateToBrackets()
                    }
                ) {
                    Text(
                        text = "Bracket",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        fontFamily = FontFamily.Serif,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }
        //Group by Conference to organize West/East
        combinedStandings.groupBy { it.conference}.forEach { mapEntry ->
            stickyHeader { ConferenceHeader(
                conferenceName = mapEntry.key.name.take(4)
            ) }
            items(mapEntry.value) { standing ->
                TeamStandingRow(
                    teamStanding = standing,
                    navigateToTeamProfile = navigateToTeamProfile
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            }
        }
    }
}

@Composable
fun ConferenceHeader(conferenceName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp) // Same as Nav-Bar
            )
    ) {
        Text(
            text = conferenceName,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.width(99.dp))
        Text(
            text = "W",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.width(45.dp))
        Text(
            text = "L",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.width(45.dp))
        Text(
            text = "L",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.width(46.dp))
        Text(
            text = "PCT",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}

@Composable
fun TeamStandingRow(teamStanding: TeamStanding, navigateToTeamProfile: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
    ) {
        Text(
            text = "${teamStanding.rank}",
            modifier = Modifier
                .weight(0.5f)
                .padding(horizontal = 5.dp)
        )
        Image(
            painter = painterResource(id = teamStanding.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(35.dp)
                .sizeIn(maxWidth = 35.dp, maxHeight = 35.dp)
                .weight(0.5f)
                .clickable { navigateToTeamProfile(teamStanding.name) }
        )
        Text(
            text = teamStanding.abbrev,
            modifier = Modifier
                .weight(1f)
                .padding(start = 5.dp)
        )
        VerticalDivider(
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            thickness = 1.dp,
            modifier = Modifier
                .height(43.dp)
                .fillMaxHeight()
                .offset(x = (-20).dp)
                .padding(end = 5.dp)
        )
        Text(text = "${teamStanding.wins}", modifier = Modifier.weight(1f))
        Text(text = "${teamStanding.losses}", modifier = Modifier.weight(1f))
        Text(
            text = dropZeroBeforeDecimal(teamStanding.winLossPercentage),
            modifier = Modifier.weight(1f)
        )
    }
}

    }
}

@Composable
fun TeamStandingRow(teamStanding: TeamStanding, navigateToTeamProfile: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
    ) {
        Text(
            text = "${teamStanding.rank}",
            modifier = Modifier
                .weight(0.5f)
                .padding(horizontal = 5.dp)
        )
        Image(
            painter = painterResource(id = teamStanding.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(35.dp)
                .sizeIn(maxWidth = 35.dp, maxHeight = 35.dp)
                .weight(0.5f)
                .clickable { navigateToTeamProfile(teamStanding.name) }
        )
        Text(
            text = teamStanding.abbrev,
            modifier = Modifier
                .weight(1f)
                .padding(start = 5.dp)
        )
        VerticalDivider(
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            thickness = 1.dp,
            modifier = Modifier
                .height(43.dp)
                .fillMaxHeight()
                .offset(x = (-20).dp)
                .padding(end = 5.dp)
        )
        Text(text = "${teamStanding.wins}", modifier = Modifier.weight(1f))
        Text(text = "${teamStanding.losses}", modifier = Modifier.weight(1f))
        Text(
            text = dropZeroBeforeDecimal(teamStanding.winLossPercentage),
            modifier = Modifier.weight(1f)
        )
    }
}


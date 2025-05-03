package com.example.jetpacktest.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jetpacktest.models.BaseballTeamStanding
import com.example.jetpacktest.ui.components.LargeDropdownMenu
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseballStandingsScreen(
    americanFlow: Flow<List<BaseballTeamStanding>>,
    nationalFlow: Flow<List<BaseballTeamStanding>>,
    updateStandingsByYear: (String) -> Unit,
    yearOptions: List<String>
) {
    val americanStandings by americanFlow.collectAsState(initial = emptyList())
    val nationalStandings by nationalFlow.collectAsState(initial = emptyList())
    var chosenYear by rememberSaveable { mutableStateOf(yearOptions.first()) }

    val combinedStandings = americanStandings + nationalStandings

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
        }

        combinedStandings.groupBy { it.league }.forEach { mapEntry ->
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                ) {
                    Text(
                        text = mapEntry.key.name.take(4),
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            items(mapEntry.value.sortedByDescending { it.wins }) { standing ->
                BaseballTeamStandingRow(teamStanding = standing)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            }
        }
    }
}

@Composable
fun BaseballTeamStandingRow(teamStanding: BaseballTeamStanding) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Text(
            text = teamStanding.rank.toString(),
            modifier = Modifier.weight(0.5f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = rememberAsyncImagePainter(model = teamStanding.logoUrl),
            contentDescription = "Logo",
            modifier = Modifier
                .size(35.dp)
                .weight(0.5f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = teamStanding.abbrev,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = teamStanding.wins.toString(),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = teamStanding.losses.toString(),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = String.format("%.3f", teamStanding.winLossPercentage),
            modifier = Modifier.weight(1f)
        )
    }
}

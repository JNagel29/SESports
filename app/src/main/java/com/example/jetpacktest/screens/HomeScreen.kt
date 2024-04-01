package com.example.jetpacktest.screens

import com.example.jetpacktest.viewmodels.HomeViewModel
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.R
import com.example.jetpacktest.models.StatLeader
import com.example.jetpacktest.ui.theme.ExpandableCard
import com.example.jetpacktest.ui.theme.JetpackTestTheme
import com.example.jetpacktest.ui.theme.LargeDropdownMenu

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    navigateToPlayerProfile: (String) -> Unit)
{
    val yearOptions = listOf("2009", "2010", "2011", "2012", "2013", "2014",
            "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022",
            "2023", "2024").reversed()
    val statOptions = listOf("PTS", "AST", "TRB", "BLK", "STL")
    //TODO:Change it so we pass just the states and lambdas, not entire Viewmodel
    val randomStat by homeViewModel.randomStatFlow.collectAsState(initial  = "")
    val statLeadersList by homeViewModel.statLeadersListFlow.collectAsState(initial = emptyList())
    val chosenStat by homeViewModel.chosenStatFlow.collectAsState(initial = "PTS")
    val chosenYear by homeViewModel.chosenYearFlow.collectAsState(initial = "2024")

    LaunchedEffect(Unit) {
        Log.d("HomeScreen", "In Unit launched effect")
        if (randomStat.isEmpty()) {
            Log.d("HomeScreen", "In Unit  launched effect, stat fetching...")
            homeViewModel.fetchRandomStat()
        }
        if (statLeadersList.isEmpty()) {
            Log.d("HomeScreen", "In Unit  launched effect, leader fetching...")
            homeViewModel.fetchStatLeaders()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        RandomStatCard(randomStat)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Stat Leaders",
            fontSize = 20.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )
        Row(Modifier.fillMaxWidth()) {
            // First dropdown menu
            LargeDropdownMenu(
                label = "Select Stat:",
                items = statOptions,
                selectedIndex = statOptions.indexOf(chosenStat),
                onItemSelected = { index, _ ->
                    val stat = statOptions[index]
                    if (stat != chosenStat) {
                        homeViewModel.updateChosenStat(stat)
                        homeViewModel.fetchStatLeaders()
                    }
                },
                modifier = Modifier.weight(1f) //Take up half of the width
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Second dropdown menu
            LargeDropdownMenu(
                label = "Select Year:",
                items = yearOptions,
                selectedIndex = yearOptions.indexOf(chosenYear),
                onItemSelected = { index, _ ->
                    val year = yearOptions[index]
                    if (year != chosenYear) {
                        homeViewModel.updateChosenYear(year)
                        homeViewModel.fetchStatLeaders()
                    }
                },
                modifier = Modifier.weight(1f) //Take up other half of width
            )
        }
        // Display Stat Leaders data
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(statLeadersList) { statLeader ->
                StatLeaderCard(
                    statLeader = statLeader,
                    chosenStat = chosenStat,
                    navigateToPlayerProfile = navigateToPlayerProfile
                )
            }
        }
    }
}

@Composable
fun RandomStatCard(randomStat: String) {
    ExpandableCard(
        title = "Random Stat of the Day",
        description = randomStat,
        backgroundColorResource = R.color.purple_lakers
    )
}

@Composable
fun StatLeaderCard(statLeader: StatLeader, chosenStat: String,
                   navigateToPlayerProfile: (String) -> Unit) {
    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp //Adds a 'shadow' effect
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Rank ${statLeader.rank}: ${statLeader.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { navigateToPlayerProfile(statLeader.name) }
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Go to profile",
                    tint = Color.Blue,
                    modifier = Modifier.clickable { navigateToPlayerProfile(statLeader.name) }
                )
            }
            Text(
                text = "$chosenStat: ${statLeader.statValue}",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    JetpackTestTheme {
        //HomeScreen()
    }
}


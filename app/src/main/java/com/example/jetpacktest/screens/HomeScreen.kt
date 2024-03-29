package com.example.jetpacktest.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.models.StatLeader
import com.example.jetpacktest.ui.theme.JetpackTestTheme
import com.example.jetpacktest.ui.theme.LargeDropdownMenu

@Composable
fun HomeScreen(navigateToPlayerProfile: (String) -> Unit) {
    val databaseHandler = DatabaseHandler()
    var statLeadersList by remember { mutableStateOf<List<StatLeader>>(emptyList()) } // Default to empty list
    var chosenStat by remember { mutableStateOf("PTS") } //Default to pts
    var chosenYear by remember { mutableStateOf("2024") } //Default to 2024
    val yearOptions = listOf("2009", "2010", "2011", "2012", "2013", "2014",
            "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022",
            "2023", "2024").reversed()
    val statOptions = listOf("PTS", "AST", "TRB", "BLK", "STL")

    LaunchedEffect(Unit) {
        //Fetch 2024 PTS on launch
        databaseHandler.executeStatLeaders(chosenStat, chosenYear) { data ->
            statLeadersList = data
        }
    }
        /*
        statLeadersList = listOf(StatLeader(rank = 5, name = "Jeff", statValue = 5.0f),
            StatLeader(rank = 5, name = "Jeff", statValue = 5.0f),
            StatLeader(rank = 5, name = "Jeff", statValue = 5.0f),
            StatLeader(rank = 5, name = "Jeff", statValue = 5.0f),
            StatLeader(rank = 5, name = "Jeff", statValue = 5.0f)
        )

         */


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
                            .padding(20.dp)
    ) {
        //Call header composable
        Header()
        //Spacer between header and menu
        Spacer(modifier = Modifier.height(8.dp))
        //Custom Dropdown menus for each stat/year
        LargeDropdownMenu(
            label = "Select Stat:",
            items = statOptions,
            selectedIndex = statOptions.indexOf(chosenStat),
            onItemSelected = { index, _ ->
                val stat = statOptions[index]
                //Check if newly selected stat is different from previous
                if (stat != chosenStat) {
                    chosenStat = stat
                    databaseHandler.executeStatLeaders(stat, chosenYear) { data ->
                        statLeadersList = data
                    }
                }
            }
        )
        LargeDropdownMenu(
            label = "Select Year:",
            items = yearOptions,
            selectedIndex = yearOptions.indexOf(chosenYear),
            onItemSelected = { index, _ ->
                val year = yearOptions[index]
                //Check if newly selected year is different from previous
                if (year != chosenYear) {
                    chosenYear = year
                    databaseHandler.executeStatLeaders(chosenStat, year) { data ->
                        statLeadersList = data
                    }
                }
            }
        )
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(statLeadersList) {statLeader ->
                //Text("${statLeader.name} ${statLeader.rank} ${statLeader.statValue}\n")
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
fun Header() {
    Text(
        "Stat Leaders",
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.Serif
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

@Composable
fun TopPlayerDisplay(statLeaderList: List<StatLeader>, chosenStat: String,
                     navigateToPlayerProfile: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        //Loop through list, and for each player, make a text row with rank/name/stat val
        items(statLeaderList) { player ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                //Rank and name on the left
                Text(
                    text = "${player.rank}: ${player.name}",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.clickable {
                        // Navigate to that player's profile
                        navigateToPlayerProfile(player.name)
                    }

                )
                //Stat value on the right
                Text(
                    text = "${player.statValue} $chosenStat",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.End
                )
            }
            //Spacer between each row for readability
            Spacer(modifier = Modifier.height(40.dp))
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


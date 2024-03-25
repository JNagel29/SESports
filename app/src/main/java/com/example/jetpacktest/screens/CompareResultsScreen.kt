package com.example.jetpacktest.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.HeadshotHandler
import com.example.jetpacktest.R
import com.example.jetpacktest.models.Player
import com.google.android.gms.dtdi.core.Extra


@Composable
fun CompareResultsScreen(playerName1: String, playerName2: String, navigateBack: () -> Unit) {
    // Instantiate a headshot handler that we'll use to fetch url by name
    val headshotHandler = HeadshotHandler()
    // Context that we pass into fetchImageUrl for Volley
    val context = LocalContext.current
    // We instantiate imgUrl with mutableStateOf, so the screen recomposes automatically when it changes
    var imgUrl1 by remember { mutableStateOf("") } // Default to ""
    var imgUrl2 by remember { mutableStateOf("") } // Default to ""
    // DatabaseHandler to fetch years for dropdown menu and fetch the actual data
    val databaseHandler = remember { DatabaseHandler() }
    // Variable to track if dropdown is expanded
    var expandedYear by remember { mutableStateOf(false) }
    // Variable we'll use to create dropdown, will need to fetch every year player has played thru DB
    var yearsList1 by remember { mutableStateOf<List<String>>(emptyList()) } // Default to empty list
    var yearsList2 by remember { mutableStateOf<List<String>>(emptyList()) } // Default to empty list
    var chosenYear1 by remember { mutableStateOf("") } // Default to nothing since dynamic
    var chosenYear2 by remember { mutableStateOf("") } // Default to nothing since dynamic
    // Variable for Player objects that will hold the data about the players
    var playerObj1 by remember { mutableStateOf(Player()) }
    var playerObj2 by remember { mutableStateOf(Player()) }
    // Variable to track whether to show the player data table
    var showPlayerData1 by remember { mutableStateOf(false) }
    var showPlayerData2 by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // On first launch, fetch the headshots and assign imgUrls to results using lambda callback
        headshotHandler.fetchImageUrl(playerName = playerName1, context = context) { result ->
            imgUrl1 = result
        }
        headshotHandler.fetchImageUrl(playerName = playerName2, context = context) { result ->
            imgUrl2 = result
        }
        // Also, fetch the list of years that we'll use to populate dropdown menu
        databaseHandler.executeYears(playerName = playerName1) { result ->
            yearsList1 = result
            if (yearsList1.isNotEmpty()) {
                chosenYear1 = yearsList1.first()
                // Fetch the player data for the chosen year
                databaseHandler.executePlayerData(playerName1, chosenYear1) { data ->
                    playerObj1 = data
                    showPlayerData1 = true
                }
            }
        }
        databaseHandler.executeYears(playerName = playerName2) { result ->
            yearsList2 = result
            if (yearsList2.isNotEmpty()) {
                chosenYear2 = yearsList2.first()
                // Fetch the player data for the chosen year
                databaseHandler.executePlayerData(playerName2, chosenYear2) { data ->
                    playerObj2 = data
                    showPlayerData2 = true
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Player 1 data on the left
        Column(
            modifier = Modifier.fillMaxWidth(1f)
        ) {
            // Display player 1 name and headshot
            NameAndHeadshotCompare(playerName1, imgUrl1, headshotHandler)
            // Display player 1 data
            if (showPlayerData1) {
                PlayerDataTable(playerObj1)
            }
        }
        // Player 2 data on the right
        Column(
            modifier = Modifier.fillMaxWidth(1f)
        ) {
            // Display player 2 name and headshot
            NameAndHeadshotCompare(playerName2, imgUrl2, headshotHandler)
            // Display player 2 data
            if (showPlayerData2) {
                PlayerDataTable(playerObj2)
            }
        }
    }
}

@Composable
fun NameAndHeadshotCompare(playerName: String, imgUrl: String, headshotHandler: HeadshotHandler) {
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = playerName,
                fontSize = 26.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold
            )
            // Now use our headshot handler to compose the image using that URL
            headshotHandler.ComposeImage(
                imgToCompose = imgUrl,
                contentDesc = playerName,
                width = 200.dp,
                height = 200.dp
            )
        }
    }


    @Composable
    fun PlayerDataTable(playerObj: Player) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Header
            Text(
                "Player Averages", fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        // List of values
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                PlayerDataRow("Year", playerObj.year.toString())
                PlayerDataRow("Position", playerObj.position)
                PlayerDataRow("Team", playerObj.team)
                PlayerDataRow("Points", playerObj.points.toString())
                PlayerDataRow("Assists", playerObj.assists.toString())
                PlayerDataRow("Steals", playerObj.steals.toString())
                PlayerDataRow("Blocks", playerObj.blocks.toString())
                PlayerDataRow("Rebounds", playerObj.totalRebounds.toString())
                PlayerDataRow("Turnovers", playerObj.turnovers.toString())
                PlayerDataRow("Fouls", playerObj.personalFouls.toString())
                PlayerDataRow("Mins. Played", playerObj.minutesPlayed.toString())
                PlayerDataRow("FG", playerObj.fieldGoals.toString())
                PlayerDataRow("FGA", playerObj.fieldGoalAttempts.toString())
                PlayerDataRow("FG%", playerObj.fieldGoalPercent.toString())
                PlayerDataRow("3P ", playerObj.threePointers.toString())
                PlayerDataRow("3PA", playerObj.threePointerAttempts.toString())
                PlayerDataRow("3P%", playerObj.threePointPercent.toString())
                PlayerDataRow("2P", playerObj.twoPointers.toString())
                PlayerDataRow("2PA", playerObj.twoPointerAttempts.toString())
                PlayerDataRow("2P%", playerObj.twoPointPercent.toString())
                PlayerDataRow("EFG%", playerObj.effectiveFieldGoalPercent.toString())
                PlayerDataRow("ORB", playerObj.offensiveRebounds.toString())
                PlayerDataRow("DRB", playerObj.defensiveRebounds.toString())
            }
        }
    }

    @Composable
    fun PlayerDataRow(label: String, value: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                label,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                value,
                fontSize = 22.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
    }

    @Composable
    fun ReturnToSearchHeader(navigateBack: () -> Unit) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    navigateBack()
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_arrow_back_ios_new_24),
                    contentDescription = "Back"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Return to Previous",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}
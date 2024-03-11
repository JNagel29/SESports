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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.R
import com.example.jetpacktest.HeadshotHandler
import com.example.jetpacktest.models.Player
//Lazy Table
import eu.wewox.lazytable.LazyTable
import eu.wewox.lazytable.LazyTableItem
import eu.wewox.lazytable.lazyTableDimensions

@Composable
fun ProfileScreen(playerName: String, navigateBack: () -> Unit) {
    //Instantiate a headshot handler that we'll use to fetch url by name
    val headshotHandler = HeadshotHandler()
    //Context that we pass into fetchImageUrl for Volley
    val context = LocalContext.current
    //We instantiate imgUrl with mutableStateOf, so the screen recomposes automatically when it changes
    var imgUrl by remember { mutableStateOf("") } // Default to ""
    //DatabaseHandler to fetch years for dropdown menu and fetch the actual data
    val databaseHandler = remember { DatabaseHandler() }
    //Variable to track if dropdown is expanded
    var expandedYear by remember { mutableStateOf(false) }
    //Variable we'll use to create dropdown, will need to fetch every year player has played thru DB
    //We use mutableStateOf for both, b/c when either changes, we need to recompose
    var yearsList by remember { mutableStateOf<List<String>>(emptyList()) } // Default to empty list
    var chosenYear by remember { mutableStateOf("") } //Default to nothing since dynamic
    //Change icon arrow depending on if dropdown is expanded or not
    val iconYear = if (expandedYear) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    //Variable for Player object that will hold the data about this player
    // Variable to track whether to show the player data table
    var showPlayerData by remember { mutableStateOf(false) }
    // Variable to hold the player data inside object
    var playerObj by remember { mutableStateOf(Player()) }

    LaunchedEffect(Unit) {
        //On first launch, fetch the headshot and assign imgUrl to result using lambda callback
        headshotHandler.fetchImageUrl(playerName = playerName, context = context) { result ->
            imgUrl = result // Update img url with parameter result you pass via callback
        }
        //Also, fetch the list of years that we'll use to populate dropdown menu
        databaseHandler.executeYears(playerName = playerName) {result ->
            yearsList = result // Update years list with parameter result you pass via callback
            //Also, default the chosenYear to the most recent year
            if (yearsList.isNotEmpty()) {
                chosenYear = yearsList.first()
            }
        }
        //Finally, by default we want to display the current year's stats
        databaseHandler.executePlayerData(playerName, chosenYear) { data ->
            playerObj = data
            showPlayerData = true // Show the player data table
        }

    }

    Box(modifier = Modifier.fillMaxSize()) {
        //Have to wrap inside column for Spacer to work
        Column(modifier = Modifier.fillMaxSize()) {
            //Calls composable that sets up our back header to go back to search
            ReturnToSearchHeader(navigateBack = navigateBack)
            //Adds space between header and actual data
            Spacer(modifier = Modifier.height(15.dp))
            //Now, we display the name and headshot, need to pass in name, url, and headshotHandler
            NameAndHeadshot(
                playerName = playerName,
                //imgUrl = "https://cdn.nba.com/headshots/nba/latest/1040x760/1628366.png"
                imgUrl = imgUrl,
                headshotHandler
            )
            //Now, our dropdown menu TODO: Might wanna separate this into own composable
            //Creates the button to expand dropdown menu for stats
            OutlinedButton(
                onClick = { expandedYear = !expandedYear },
                modifier = Modifier.fillMaxWidth()
            ) {
                //Put text and up/down arrow in same row inside button
                Text("Select Year: $chosenYear")
                Icon(
                    iconYear,
                    "Year Select",
                    Modifier.align(Alignment.CenterVertically)
                )
            }
            //We create a box to enclose the menu AND all the items
            Box {
                DropdownMenu(
                    expanded = expandedYear,
                    onDismissRequest = { expandedYear = false },
                    modifier = Modifier
                        .width(80.dp) // Limit width
                        .height(250.dp), // Limit height so user can scroll when many years
                offset = DpOffset(300.dp, 0.dp) // Move items 300dp to the right
                ) {
                    //For each year in the dynamic list we made, create a dropdown menu item
                    yearsList.forEach { year ->
                        DropdownMenuItem(
                            text = { Text(text = year) },
                            onClick = {
                                //Update year, close menu and fetch new data on click
                                chosenYear = year
                                expandedYear = false
                                //TODO: uncomment and add else for bdl api
                                //if (chosenYear != "2024") {
                                    databaseHandler.executePlayerData(playerName, chosenYear) { data ->
                                        playerObj = data
                                        showPlayerData = true // Show the player data table
                                    }
                               // }
                            }
                        )
                    }
                }
            }
            //Show PlayerDataTable based on showPlayerData
            if (showPlayerData) {
                PlayerDataTable(playerObj)
            }
        }
    }
}

@Composable
fun PlayerDataTable(playerObj: Player) {
    //Header and slight amount of vertical space
    Text("Player Averages", fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(4.dp))
    //Now the actual list of values
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Pass in list of rows with Label and Value
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
        //Prints bolded label (Year, Position, etc.)
        Text(
            label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(100.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        //Prints actual value after the spacer
        Text(value, fontSize = 18.sp)
    }
}


@Composable
fun NameAndHeadshot(playerName: String, imgUrl: String, headshotHandler: HeadshotHandler) {
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = playerName,
                fontSize = 22.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )
            //Now use our headshot handler to compose the image using that URL
            headshotHandler.ComposeImage(
                imgToCompose = imgUrl,
                contentDesc = playerName,
                width = 200.dp,
                height = 200.dp
            )
        }
    }
}

@Composable
fun ReturnToSearchHeader(navigateBack: () -> Unit) {
    Column(
        //Arrange it on top TODO: might remove this?
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                navigateBack()
            }
        ) {
            //Puts icon and back text on same row (one that is clickable b/c of modifier above)
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.baseline_arrow_back_ios_new_24),
                contentDescription = "Back"
            )
            //Spacer between the icon and text
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Return to Search",
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif
            )
        }
    }
}

@Composable
fun LazyTableTest() {
    //Now, we create our lazy table (TODO: put in own func and implement dynamic)
    val columns = 10
    val rows = 10
    LazyTable(
        dimensions = lazyTableDimensions(48.dp, 32.dp)
    ) {
        items(
            count = columns * rows,
            layoutInfo = {
                LazyTableItem(
                    column = it % columns,
                    row = it / columns,
                )
            }
        ) { index ->
            Text(text = "#$index")
        }
    }
}
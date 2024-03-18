package com.example.jetpacktest.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.models.TopPlayer
import com.example.jetpacktest.ui.theme.JetpackTestTheme
@Composable
fun HomeScreen(navigateToPlayerProfile: (String) -> Unit) {
    //'Remember' keyword means, whenever this var changes, recompose our Home Screen
    val databaseHandler = DatabaseHandler()
    var topPlayerList by remember { mutableStateOf<List<TopPlayer>>(emptyList()) } // Default to empty list
    var chosenStat by remember { mutableStateOf("PTS") } //Default to pts
    var chosenYear by remember { mutableStateOf("2024") } //Default to 2024
    //List of years that we'll loop through for dropdown menu
    val yearOptions = listOf("2009", "2010", "2011", "2012", "2013", "2014",
            "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022",
            "2023", "2024").reversed()
    //List of stats that we'll loop through for dropdown menu
    val statOptions = listOf("PTS", "AST", "TRB", "BLK", "STL")
    //Variables to track if dropdown is expanded
    var expandedStat by remember { mutableStateOf(false) }
    var expandedYear by remember { mutableStateOf(false) }
    //Change icon arrow from up to down depending on if its open
    val iconStat = if (expandedStat) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    val iconYear = if (expandedYear) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    //On first launch, fetch the data initially for the default PTS and 2024, so user doesn't have
    //to click on the spinner to get the data
    LaunchedEffect(Unit) {
        databaseHandler.executeStatLeaders(chosenStat, chosenYear) { data ->
            topPlayerList = data
        }
    }

    //Put everything in a column
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
                            .padding(20.dp)
    ) {
        //Call header composable
        Header()
        //Spacer between header and menu
        Spacer(modifier = Modifier.height(8.dp))
        //Set up stat dropdown (need to pass in lambdas to change variables
        StatDropdown(
            chosenStat = chosenStat,
            chosenYear = chosenYear,
            iconStat = iconStat,
            expandedStat = expandedStat,
            statOptions = statOptions,
            databaseHandler = databaseHandler,
            onToggleExpandStat = { expandedStat = !expandedStat },
            onCloseStatMenu = { expandedStat = false },
            onChosenStatUpdate = {stat ->
              chosenStat = stat
            },
            onTopPlayerListUpdate = { updatedList ->
                topPlayerList = updatedList
            }
        )
        //Now, we similarly set up year dropdown
        YearDropdown(
            chosenStat = chosenStat,
            chosenYear = chosenYear,
            iconYear = iconYear,
            expandedYear = expandedYear,
            yearOptions = yearOptions,
            databaseHandler = databaseHandler,
            onToggleExpandYear = { expandedYear = !expandedYear },
            onCloseYearMenu = { expandedYear = false },
            onChosenYearUpdate = {year ->
                chosenYear = year
            },
            onTopPlayerListUpdate = { updatedList ->
                topPlayerList = updatedList
            }
        )
        //Finally, we can display all our data in a lazy column, (we pass in lambda to go to player)
        TopPlayerDisplay(topPlayerList, chosenStat, navigateToPlayerProfile)
    }
}

@Composable
fun Header() {
    Text(
        "Stat Leaders",
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Composable
fun StatDropdown(
    chosenStat: String,
    chosenYear: String,
    iconStat: ImageVector,
    expandedStat: Boolean,
    databaseHandler: DatabaseHandler,
    statOptions: List<String>,
    onToggleExpandStat: () -> Unit,
    onCloseStatMenu: () -> Unit,
    onChosenStatUpdate: (String) -> Unit,
    onTopPlayerListUpdate: (List<TopPlayer>) -> Unit
) {
    //Creates the button to expand dropdown menu for stats
    OutlinedButton(
        onClick = { onToggleExpandStat() },
        modifier = Modifier.fillMaxWidth()
    ) {
        //Put text and up/down arrow in same row inside button
        Text("Select Stat: $chosenStat")
        Icon(
            iconStat,
            "Stat Select",
            Modifier.align(Alignment.CenterVertically)
        )
    }
    //We create a box to enclose the menu AND all the items
    Box {
        DropdownMenu(
            expanded = expandedStat,
            onDismissRequest = { onCloseStatMenu() },
            modifier = Modifier
                .width(80.dp), // Limit width
            offset = DpOffset(300.dp, 0.dp) // Move items 300dp to the right
        ) {
            //For each stat in the list, create a dropdown menu item
            statOptions.forEach { stat ->
                DropdownMenuItem(
                    text = { Text(text = stat) },
                    onClick = {
                        //Update stat, close menu and fetch new data on click
                        onChosenStatUpdate(stat)
                        onCloseStatMenu()
                        databaseHandler.executeStatLeaders(chosenStat, chosenYear) { data ->
                            onTopPlayerListUpdate(data)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun YearDropdown(
    chosenStat: String,
    chosenYear: String,
    iconYear: ImageVector,
    expandedYear: Boolean,
    databaseHandler: DatabaseHandler,
    yearOptions: List<String>,
    onToggleExpandYear: () -> Unit,
    onCloseYearMenu: () -> Unit,
    onChosenYearUpdate: (String) -> Unit,
    onTopPlayerListUpdate: (List<TopPlayer>) -> Unit
) {
    //Creates the button to expand dropdown menu for years
    OutlinedButton(
        onClick = { onToggleExpandYear() },
        modifier = Modifier.fillMaxWidth()
    ) {
        //Put text and up/down arrow in same row inside button
        Text("Select Year: $chosenYear")
        Icon(
            iconYear,
            "contentDescription",
            Modifier.align(Alignment.CenterVertically)
        )
    }
    //We create a box to enclose the menu AND all the items
    Box {
        DropdownMenu(
            expanded = expandedYear,
            onDismissRequest = { onCloseYearMenu() },
            modifier = Modifier
                .width(80.dp) //Limit width
                .height(250.dp), // Limit height so user can scroll
            offset = DpOffset(300.dp, 0.dp) // Move menu items 300 dp to right
        ) {
            //For each year in the list, create a dropdown menu item
            yearOptions.forEach { year ->
                DropdownMenuItem(
                    text = { Text(text = year) },
                    onClick = {
                        //Update year, close menu and fetch new data on click
                        onChosenYearUpdate(year)
                        onCloseYearMenu()
                        databaseHandler.executeStatLeaders(chosenStat, chosenYear) { data ->
                            onTopPlayerListUpdate(data)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TopPlayerDisplay(topPlayerList: List<TopPlayer>, chosenStat: String,
                     navigateToPlayerProfile: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        //Loop through topPlayerList, and for each player, make a text row with rank/name/stat val
        items(topPlayerList) { player ->
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
                    text = "${player.stat} $chosenStat",
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

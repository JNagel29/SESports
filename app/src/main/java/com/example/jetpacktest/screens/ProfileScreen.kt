package com.example.jetpacktest.screens

import ReturnToPreviousHeader
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.ApiHandler
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.R
import com.example.jetpacktest.HeadshotHandler
import com.example.jetpacktest.models.NbaTeam
import com.example.jetpacktest.ui.components.LargeDropdownMenu
import com.example.jetpacktest.models.Player
import com.example.jetpacktest.ui.components.CircularLoadingIcon

@Composable
fun ProfileScreen(playerName: String, navigateBack: () -> Unit) {
    val headshotHandler = HeadshotHandler()
    val databaseHandler = DatabaseHandler()
    //val context = LocalContext.current
    val apiHandler = ApiHandler()
    //var imgUrl by remember { mutableStateOf("") }
    var imgId by rememberSaveable { mutableIntStateOf(-1) }
    var yearsList by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var chosenYear by rememberSaveable { mutableStateOf("") }
    var player by rememberSaveable { mutableStateOf(Player()) }
    var showExpandedData by rememberSaveable { mutableStateOf(false) }
    var isFetching by rememberSaveable { mutableStateOf(true) }

    //TODO: Add remember savable instead of view model maybe??
    LaunchedEffect(Unit) {
        /*
        //TODO: Commenting rn since blocked out anyway
        headshotHandler.fetchImageId(playerName) { result ->
            imgId = result
        }
        */
        if (yearsList.isEmpty()) {
            databaseHandler.executeYears(playerName = playerName) {result ->
                yearsList = result // Update years list with parameter result you pass via callback
                //Also, default the chosenYear to the most recent year
                if (yearsList.isNotEmpty()) {
                    chosenYear = yearsList.first()
                    //Then, fetch all our data for that most recent year
                    databaseHandler.executePlayerData(playerName, chosenYear) { data ->
                        player = data
                        isFetching = false
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        //Have to wrap inside column for Spacer to work
        Column(modifier = Modifier.fillMaxSize()) {
            //Calls composable that sets up our back header to go back to search
            ReturnToPreviousHeader(navigateBack = navigateBack)
            //Adds space between header and actual data
            Spacer(modifier = Modifier.height(15.dp))
            if (isFetching) {
                CircularLoadingIcon()
            }
            else {
                NameAndHeadshot(
                    playerName = playerName,
                    imgId = imgId,
                    team =  player.team,
                    position = player.position,
                    headshotHandler = headshotHandler
                )
                HorizontalDivider(thickness = 2.dp, color = Color.White)
                MainStatBoxes(player = player)
                //Custom Dropdown menu for each year
                LargeDropdownMenu(
                    label = "Select Year:",
                    items = yearsList,
                    selectedIndex = yearsList.indexOf(chosenYear),
                    onItemSelected = { index, _ ->
                        chosenYear = yearsList[index]
                        if (chosenYear != "2024") {
                            databaseHandler.executePlayerData(playerName, chosenYear) { data ->
                                player = data
                            }
                        }
                        else {
                            databaseHandler.executePlayerData(playerName, chosenYear) { data ->
                                player = data
                            }
                            /*
                            apiHandler.fetchPlayerData(
                                context,
                                onResult = {data ->
                                    player = data
                                }
                            )
                             */
                        }
                    }
                )
                //Now, compose our button that toggles the extra data in list
                ToggleFurtherStats(showExpandedData = showExpandedData,
                    onClick = { showExpandedData = !showExpandedData })
                //Show the extra data only if user toggled via button above (and player has stats)
                if (showExpandedData && player.points != -1.0f) {
                    PlayerDataTable(player)
                }
            }
        }
    }
}

@Composable
fun NameAndHeadshot(
    playerName: String,
    imgId: Int,
    team: String,
    position: String,
    headshotHandler: HeadshotHandler
) {
    var isFavorite by remember { mutableStateOf(false) }
    //We use a box to color the background
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(
                    //Split by / in case two teams
                    NbaTeam.teamColorsMap[team.split("/")[0]] ?: R.color.purple_lakers
                )
            )
            .offset(y = 20.dp) //Move image down a bit
    ) {
        val imgUrl = if (imgId == -1 || imgId == 0) "DEFAULT"
        else "https://cdn.nba.com/headshots/nba/latest/1040x760/$imgId.png"
        //This row will hold the headshot and player name
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            headshotHandler.ComposeImage(
                imgToCompose = imgUrl,
                contentDesc = playerName,
                width = 200.dp,
                height = 200.dp
            )
            //Spacer between headshot and text
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Row(modifier = Modifier.padding(end = 8.dp)) {
                    Text(
                        text = "$team | $position",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector =  if (isFavorite) Icons.Filled.Star
                        else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Yellow else Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = { isFavorite = !isFavorite })
                    )
                }
                Text(
                    text = playerName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun MainStatBoxes(player: Player) {
    //Bool used to print N/A if player has no stats (-1 points)
    val isEmptyStats = (player.points == -1.0f)
    //Horizontal bar with PPG, RPG, APG (most important stats)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(
                    NbaTeam.teamColorsMap[player.team.split("/")[0]]
                        ?: R.color.purple_lakers
                )
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatBox(
            label = "PPG",
            value = if (!isEmptyStats) player.points.toString() else "N/A"
        )
        VerticalDivider(modifier = Modifier.height(50.dp),
            thickness = 2.dp, color = Color.White)
        StatBox(
            label = "RPG",
            value = if (!isEmptyStats) player.totalRebounds.toString() else "N/A"
        )
        VerticalDivider(modifier = Modifier.height(50.dp),
            thickness = 2.dp, color = Color.White)
        StatBox(
            label = "APG",
            value = if (!isEmptyStats) player.assists.toString() else "N/A"
        )
    }
}

@Composable
fun StatBox(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.Bold,
            color = Color.White)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold,
            color = Color.White)
    }
}

@Composable
fun ToggleFurtherStats(onClick: () -> Unit, showExpandedData: Boolean) {
    //Toggles the extra stats
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        OutlinedButton(
            onClick = onClick
        ) {
            Text(if (showExpandedData) "Shrink Further Stats" else "Expand Further Stats")
        }
    }
}

@Composable
fun PlayerDataTable(player: Player) {
    //Slight amount of vertical space
    Spacer(modifier = Modifier.height(4.dp))
    //Now the actual list of values
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Pass in list of rows with Label and Value
        item {
            //PlayerDataRow("Year", player.year.toString())
            //PlayerDataRow("Position", player.position)
            //PlayerDataRow("Team", player.team)
            //PlayerDataRow("Points", player.points.toString())
            //PlayerDataRow("Assists", player.assists.toString())
            //PlayerDataRow("Rebounds", player.totalRebounds.toString())
            PlayerDataRow("Steals", player.steals.toString())
            PlayerDataRow("Blocks", player.blocks.toString())
            PlayerDataRow("FG", player.fieldGoals.toString())
            PlayerDataRow("FGA", player.fieldGoalAttempts.toString())
            PlayerDataRow("FG%", "%.1f%%".format(player.fieldGoalPercent * 100))
            PlayerDataRow("3P ", player.threePointers.toString())
            PlayerDataRow("3PA", player.threePointerAttempts.toString())
            PlayerDataRow("3P%", "%.1f%%".format(player.threePointPercent * 100))
            PlayerDataRow("Turnovers", player.turnovers.toString())
            PlayerDataRow("Fouls", player.personalFouls.toString())
            PlayerDataRow("Mins. Played", player.minutesPlayed.toString())
            PlayerDataRow("2P", player.twoPointers.toString())
            PlayerDataRow("2PA", player.twoPointerAttempts.toString())
            PlayerDataRow("2P%", "%.1f%%".format(player.twoPointPercent * 100))
            PlayerDataRow("EFG%", "%.1f%%".format(player.effectiveFieldGoalPercent * 100))
            //PlayerDataRow("ORB", player.offensiveRebounds.toString())
            //PlayerDataRow("DRB", player.defensiveRebounds.toString())
        }
    }
}

@Composable
fun PlayerDataRow(label: String, value: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            //Prints label aligned to the left
            Text(
                label,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            //Prints actual value aligned to the right
            Text(
                value,
                fontSize = 22.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider()
    }

}

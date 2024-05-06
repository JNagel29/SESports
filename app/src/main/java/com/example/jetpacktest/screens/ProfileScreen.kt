package com.example.jetpacktest.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.ApiHandler
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.FavoritesHandler
import com.example.jetpacktest.R
import com.example.jetpacktest.HeadshotHandler
import com.example.jetpacktest.getFantasyRating
import com.example.jetpacktest.models.TeamMaps
import com.example.jetpacktest.ui.components.LargeDropdownMenu
import com.example.jetpacktest.models.Player
import com.example.jetpacktest.models.PlayerPersonalInfo
import com.example.jetpacktest.ui.components.CircularLoadingIcon
import com.example.jetpacktest.ui.components.ExpandableCategory
import com.example.jetpacktest.ui.components.ReturnToPreviousHeader

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileScreen(
    playerName: String,
    navigateBack: () -> Unit,
    getPreviousScreenName: () -> (String?),
    showSnackBar: (String) -> Unit
) {
    val headshotHandler = HeadshotHandler()
    val databaseHandler = DatabaseHandler()
    val apiHandler = ApiHandler()
    val context = LocalContext.current
    val favoritesHandler = FavoritesHandler(context)
    var imgId by rememberSaveable { mutableIntStateOf(-1) }
    var yearsList by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var chosenYear by rememberSaveable { mutableStateOf("") }
    var player by rememberSaveable { mutableStateOf(Player()) }
    var isFetchingStats by rememberSaveable { mutableStateOf(true) }
    var isFetchingInfo by rememberSaveable { mutableStateOf(true) }
    var injuryStartDate by rememberSaveable { mutableStateOf("")}
    var playerPersonalInfo by rememberSaveable { mutableStateOf(PlayerPersonalInfo(emptyList())) }

    LaunchedEffect(Unit) {
        //We use conditionals to make sure these aren't re-fetched on screen swaps
        if (imgId == -1) {
            Log.d("ProfileScreen", "Updating headshot")
            headshotHandler.fetchImageId(playerName) { result ->
                imgId = result
            }
        }
        if (yearsList.isEmpty()) {
            Log.d("ProfileScreen", "Fetching new years")
            databaseHandler.executeYears(playerName = playerName) {result ->
                yearsList = result // Update years list with parameter result you pass via callback
                //Also, default the chosenYear to the most recent year
                if (yearsList.isNotEmpty()) {
                    Log.d("ProfileScreen", "Fetching new profile data")
                    chosenYear = yearsList.first()
                    //Then, fetch all our data for that most recent year
                    databaseHandler.executePlayerData(playerName, chosenYear) { data ->
                        player = data
                        isFetchingStats = false
                    }
                }
            }
        }
        if (injuryStartDate == "") {
            Log.d("ProfileScreen", "Checking if injured or not")
            databaseHandler.executeCheckInjuredStartDate(playerName = playerName) { result ->
                injuryStartDate = result
            }
        }
        if (playerPersonalInfo.data.isEmpty()) {
            Log.d("ProfileScreen", "Fetching personal info")
            playerPersonalInfo = apiHandler.fetchPlayerInfo(playerName = playerName)
            isFetchingInfo = false
        }
    }


    LazyColumn(modifier = Modifier.fillMaxSize()) {
        stickyHeader {
            ReturnToPreviousHeader(
                navigateBack = navigateBack,
                label = getPreviousScreenName()?.let { screenName ->
                    if (screenName.contains("team")) "Team Profile"
                    else screenName.dropLast(6)
                } ?: ""
            )
        }
        item {
            Spacer(modifier = Modifier.height(15.dp))
        }

        if (isFetchingStats || isFetchingInfo) {
            item { CircularLoadingIcon() }
        } else {
            item {
                NameAndHeadshot(
                    playerName = playerName,
                    imgId = imgId,
                    team = player.team,
                    position = player.position,
                    jerseyNumber = playerPersonalInfo.data[0].jerseyNumber,
                    injuryStartDate = injuryStartDate,
                    headshotHandler = headshotHandler,
                    favoritesHandler = favoritesHandler,
                    showSnackBar = showSnackBar
                )
            }

            item {
                HorizontalDivider(thickness = 2.dp, color = Color.White)
            }

            item {
                MainStatBoxes(player = player)
            }

            item {
                HorizontalDivider(thickness = 2.dp, color = Color.White)
            }

            item {
                InfoStatBoxes(
                    playerPersonalInfo = playerPersonalInfo,
                    color = colorResource(
                        TeamMaps.teamColorsMap[player.team.split("/")[0]]
                            ?: R.color.purple_lakers
                    )
                )
            }

            item {
                LargeDropdownMenu(
                    label = "Select Year:",
                    items = yearsList,
                    selectedIndex = yearsList.indexOf(chosenYear),
                    modifier = Modifier.padding(top = 5.dp, start = 5.dp, end = 5.dp),
                    onItemSelected = { index, _ ->
                        chosenYear = yearsList[index]
                        databaseHandler.executePlayerData(
                            playerName,
                            chosenYear
                        ) { data ->
                            player = data
                        }
                    }
                )
            }
            if (player.points != -1.0f) {
                item {
                    PlayerStatisticTable(player)
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
    jerseyNumber: String,
    injuryStartDate: String,
    headshotHandler: HeadshotHandler,
    favoritesHandler: FavoritesHandler,
    showSnackBar: (String) -> Unit
) {
    var isFavorite by remember {
        mutableStateOf(favoritesHandler.getFavoritePlayers().contains(playerName))
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(
                    //Split by / in case two teams
                    TeamMaps.teamColorsMap[team.split("/")[0]] ?: R.color.purple_lakers
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
                        text = "$team | $position | #$jerseyNumber",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star
                                      else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Yellow else Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                onClick = {
                                    isFavorite = !isFavorite
                                    if (isFavorite) {
                                        val favoriteAdded =
                                            favoritesHandler.addFavoritePlayer(playerName)
                                        if (!favoriteAdded) {
                                            showSnackBar("Sorry, too many favorites!")
                                            isFavorite = !isFavorite
                                        }
                                    } else {
                                        favoritesHandler.removeFavoritePlayer(playerName)
                                    }
                                }
                            )
                    )

                }
                Text(
                    text = playerName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(10.dp))
                //Display injury notifier if so
                if (injuryStartDate != "N/A") {
                    Text(
                        text = "Injured since ${injuryStartDate.dropLast(9)}",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun StatBox(
    label: String,
    value: String,
    labelFontSize: TextUnit = 16.sp,
    valueFontSize: TextUnit = 16.sp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = labelFontSize,
            color = Color.White)
        Text(value, fontSize = valueFontSize, fontWeight = FontWeight.Bold,
            color = Color.White)
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
                    TeamMaps.teamColorsMap[player.team.split("/")[0]]
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
fun InfoStatBoxes(playerPersonalInfo: PlayerPersonalInfo, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = color),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatBox(
            label = "Height/Weight",
            value = "${playerPersonalInfo.data[0].height} | " +
                    playerPersonalInfo.data[0].weight,
            labelFontSize = 14.sp,
            valueFontSize = 16.sp
        )
        VerticalDivider(modifier = Modifier.height(50.dp),
            thickness = 2.dp, color = Color.White)
        StatBox(
            label = "Draft",
            value = if (playerPersonalInfo.data[0].draftYear != 0)
                "${playerPersonalInfo.data[0].draftYear} " +
                        "Round ${playerPersonalInfo.data[0].draftRound} " +
                        "Pick ${playerPersonalInfo.data[0].draftNumber}"
            else "Undrafted",
            labelFontSize = 14.sp,
            valueFontSize = 16.sp
        )
    }
    HorizontalDivider(thickness = 2.dp, color = Color.White)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = color),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatBox(
            label = "College",
            value = playerPersonalInfo.data[0].college,
            labelFontSize = 14.sp,
            valueFontSize = 16.sp
        )
        VerticalDivider(modifier = Modifier.height(50.dp),
            thickness = 2.dp, color = Color.White)
        StatBox(
            label = "Country",
            value = playerPersonalInfo.data[0].country,
            labelFontSize = 14.sp,
            valueFontSize = 16.sp
        )
    }
}

@Composable
fun PlayerStatisticTable(player: Player) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        ExpandableCategory("Shooting Splits") {
            Column(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                PlayerDataRow("Fantasy Rating", getFantasyRating(player))
                PlayerDataRow("Field Goals", player.fieldGoals.toString())
                PlayerDataRow("Field Goal Attempts", player.fieldGoalAttempts.toString())
                PlayerDataRow("Field Goal %", "%.1f%%"
                    .format(player.fieldGoalPercent * 100))
                PlayerDataRow("3 Pointers", player.threePointers.toString())
                PlayerDataRow("3 Point Attempts", player.threePointerAttempts.toString())
                PlayerDataRow("3 Point %", "%.1f%%"
                    .format(player.threePointPercent * 100))
                PlayerDataRow("2 Pointers", player.twoPointers.toString())
                PlayerDataRow("2 Point Attempts", player.twoPointerAttempts.toString())
                PlayerDataRow("2 Point %", "%.1f%%"
                    .format(player.twoPointPercent * 100))
                PlayerDataRow("Free Throws", player.freeThrows.toString())
                PlayerDataRow("Free Throw Attempts", player.freeThrowAttempts.toString())
                PlayerDataRow("Free Throw %", "%.1f%%"
                    .format(player.freeThrowPercent * 100))
                PlayerDataRow(
                    "Effective FG%",
                    "%.1f%%".format(player.effectiveFieldGoalPercent * 100)
                )
            }
        }

        ExpandableCategory("Defensive Efficiency and Usage") {
            Column(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                PlayerDataRow("Steals", player.steals.toString())
                PlayerDataRow("Blocks", player.blocks.toString())
                PlayerDataRow("Fouls", player.personalFouls.toString())
                PlayerDataRow("Turnovers", player.turnovers.toString())
                PlayerDataRow("Mins. Played", player.minutesPlayed.toString())
                PlayerDataRow("Games Started", player.gamesStarted.toString())
            }
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
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            value,
            fontSize = 16.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
    HorizontalDivider()
}

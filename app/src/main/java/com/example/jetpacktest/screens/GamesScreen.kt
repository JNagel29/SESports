package com.example.jetpacktest.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.GamesHandler
import com.example.jetpacktest.models.Game
import com.example.jetpacktest.models.NbaTeam
import com.foreverrafs.datepicker.DatePickerTimeline
import java.time.ZoneId
import java.util.Calendar
import java.util.Date


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GamesScreen(navigateToTeamProfile: (String) -> Unit) {
    //Instantiate a games handler that we'll use to fetch daily games
    val gamesHandler = GamesHandler()
    //Also, instantiate empty list of games that we'll fetch on launch
    var gamesList by remember { mutableStateOf<List<Game>>(emptyList()) }
    //Context that we pass into fetchDailyGames for Volley
    val context = LocalContext.current
    // State variable to hold the selected date (default to current time)
    var selectedDate by remember {mutableStateOf(Date())}

    //Now, we use LaunchedEffect to fetch list of games when screen is up
    LaunchedEffect(Unit) {
        Log.d("GamesScreen", "Launched Effect has been activated")
        //TODO: Not working correctly, gamesList resets when nav-barring away and back
        //Only fetch data if the list is empty
        if (gamesList.isEmpty()) {
            Log.d("GamesScreen", "Games list is being fetched")
            gamesHandler.fetchDailyGames(
                currDate = Calendar.getInstance().time,
                context = context,
                onResult = { data ->
                    gamesList = data // Use callback lambda to return results asynchronously
                }
            )
        }
    }

    // Column to hold both the DatePickerTimeline and the LazyColumn
    Column(
        modifier = Modifier.fillMaxSize() // Expand to fill the available space
    ) {
        //Horizontal Date Picker to swap date
        DatePickerTimeline(
            backgroundColor = Color.LightGray,
            //Handle fetching of new games when date is changed
            onDateSelected = { selectedLocalDate ->
                //Convert LocalDate to Date
                val newSelectedDate = Date.from(
                    selectedLocalDate
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
                )
                //Now, we make sure the new date is actually different (to prevent clicking same)
                if (newSelectedDate != selectedDate) {
                    // Update the currently selected date
                    selectedDate = newSelectedDate
                    // Clear out the old games list
                    gamesList = emptyList()
                    // Fetch new games using the new date
                    gamesHandler.fetchDailyGames(
                        currDate = selectedDate,
                        context = context
                    ) { data ->
                        gamesList = data // Use callback lambda to return results asynchronously
                    }
                }
            },
            //Clickable label that takes user back to current date
            todayLabel = {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "Today",
                    color = Color.Black,
                )
            }
        )
        //Lazy Column where each item is a game
        LazyColumn(modifier = Modifier.weight(1f)) { // Consume remaining space
            items(gamesList) {game ->
                //Represent each game as a card (see below composable)
                GameCard(game = game, navigateToTeamProfile)
            }
        }
    }
}
@Composable
fun GameCard(game: Game, navigateToTeamProfile: (String) -> Unit) {
    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp // Adds a 'shadow' effect
        ),
        colors = CardDefaults.cardColors(
            // Sets background and text color of card
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            //Top Row that holds Home Logo, Home Score, Away Score, Away Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = game.homeLogo),
                    contentDescription = "Home Team Logo",
                    modifier = Modifier
                        .size(50.dp)
                        //On click, nav to team profile, substituting e.g. Hawks with Atlanta Hawks
                        .clickable {navigateToTeamProfile(
                            NbaTeam.shortenedNamesToFullNames[game.homeName] ?: game.homeName
                        )}
                )
                Text(
                    text = game.homeScore,
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = game.awayScore,
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Image(
                    painter = painterResource(id = game.awayLogo),
                    contentDescription = "Away Team Logo",
                    modifier = Modifier
                        .size(50.dp)
                        //On click, nav to team profile, substituting e.g. Hawks with Atlanta Hawks
                        .clickable { navigateToTeamProfile(
                            NbaTeam.shortenedNamesToFullNames[game.awayName] ?: game.awayName
                        ) }
                )
            }
            //Spacer between rows
            Spacer(modifier = Modifier.height(8.dp))
            //Bottom row that simply holds Home Name, Game Status, Away Name
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = game.homeName,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp
                )
                Text(
                    text = game.gameTime.ifEmpty { game.gameStatus },
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                )
                Text(
                    text = game.awayName,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp
                )
            }
        }
    }
}

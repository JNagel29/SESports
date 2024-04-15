package com.example.jetpacktest.screens

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.GamesHandler
import com.example.jetpacktest.models.Game
import com.example.jetpacktest.models.NbaTeam
import com.example.jetpacktest.ui.components.CircularLoadingIcon
import com.foreverrafs.datepicker.DatePickerTimeline
import java.time.ZoneId
import java.util.Date

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GamesScreen(navigateToTeamProfile: (String) -> Unit) {
    val gamesHandler = GamesHandler()
    var selectedDate by rememberSaveable { mutableStateOf(Date()) }
    var gamesList by rememberSaveable { mutableStateOf<List<Game>?>(emptyList()) }
    var isFetching by rememberSaveable { mutableStateOf(false)}
    var lastFetchedDate by remember { mutableStateOf(selectedDate) }

    LaunchedEffect(selectedDate) {
        if ( (gamesList != null && gamesList?.isEmpty()!!) || selectedDate != lastFetchedDate) {
            Log.d("GamesHandler", "Fetching new games...")
            isFetching = true
            gamesList = emptyList()
            gamesHandler.fetchGames(date = selectedDate) { data ->
                gamesList = data
                isFetching = false
                lastFetchedDate = selectedDate
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        DatePickerTimeline(
            backgroundColor = Color.LightGray,
            onDateSelected = { selectedLocalDate ->
                val newSelectedDate = Date.from(
                    selectedLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                )
                //Ensures we can't click same one
                if (newSelectedDate != selectedDate) {
                    selectedDate = newSelectedDate
                }
            },
            todayLabel = {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "Today",
                    color = Color.Black,
                )
            }
        )
        if (!isFetching) {
            if (gamesList != null) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(gamesList!!) { game ->
                        GameCard(game = game, navigateToTeamProfile)
                    }
                }
            }
            else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Sorry! No games today",
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        else CircularLoadingIcon()

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
            // Top Row that holds Home Logo, Home Score, Game Status, Away Score, Away Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = game.home_team.logo),
                    contentDescription = "Home Team Logo",
                    modifier = Modifier
                        .size(50.dp)
                        //On click, nav to team profile, substituting e.g. Hawks with Atlanta Hawks
                        .clickable {
                            navigateToTeamProfile(
                                NbaTeam.shortenedNamesToFullNames[game.home_team.name]
                                    ?: game.home_team.name
                            )
                        }
                )
                if (isGameUpcoming(game)) DisplayUpcomingInfo(game)
                else DisplayOngoingOrPreviousInfo(game)
                Image(
                    painter = painterResource(id = game.visitor_team.logo),
                    contentDescription = "Away Team Logo",
                    modifier = Modifier
                        .size(50.dp)
                        //On click, nav to team profile, substituting e.g. Hawks with Atlanta Hawks
                        .clickable {
                            navigateToTeamProfile(
                                NbaTeam.shortenedNamesToFullNames[game.visitor_team.name]
                                    ?: game.visitor_team.name
                            )
                        }
                )
            }
            // Spacer between rows
            Spacer(modifier = Modifier.height(8.dp))
            // Bottom row that simply holds Home Name, Game Status, Away Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = game.home_team.name,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                // Spacer to create gap between home and away team names
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = game.visitor_team.name,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun DisplayUpcomingInfo(game: Game) {
    Text(
        text = game.status,
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.Serif,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun DisplayOngoingOrPreviousInfo(game: Game) {
    Text(
        text = game.home_team_score.toString(),
        fontFamily = FontFamily.Serif,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
    Box {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (game.status == "Final" && game.home_team_score > game.visitor_team_score) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Left team won",
                    tint = Color.Black,
                    modifier = Modifier.size(15.dp)
                )
            }
            Text(
                text = (if (game.time.isNullOrEmpty()) game.status else game.time)!!,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            if (game.status == "Final" && game.visitor_team_score > game.home_team_score) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Right team won",
                    tint = Color.Black,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
    Text(
        text = game.visitor_team_score.toString(),
        fontFamily = FontFamily.Serif,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

private fun isGameUpcoming(game: Game): Boolean {
    return (game.status.contains("AM") || game.status.contains("PM"))
}

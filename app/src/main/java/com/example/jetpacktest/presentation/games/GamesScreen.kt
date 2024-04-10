package com.example.jetpacktest.presentation.games

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jetpacktest.common.components.CircularLoadingIcon
import com.example.jetpacktest.data.remote.dto.games.Game
import com.foreverrafs.datepicker.DatePickerTimeline
import java.time.ZoneId
import java.util.Date

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GamesScreen(
    navigateToTeamProfile: (String) -> Unit,
    viewModel: GamesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    var selectedDate by rememberSaveable { mutableStateOf(Date()) }


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
                    viewModel.fetchGames(date = newSelectedDate)
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
        if (state.error.isNotBlank()) {
            Text(
                text = state.error,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
        else if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularLoadingIcon()
            }
        }
        else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.games.data) { game ->
                    GameCard(game, navigateToTeamProfile)
                }
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
            // Top Row that holds Home Logo, Home Score, Game Status, Away Score, Away Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = game.homeTeam.logo),
                    contentDescription = "Home Team Logo",
                    modifier = Modifier
                        .size(50.dp)
                        //On click, nav to team profile, substituting e.g. Hawks with Atlanta Hawks
                        .clickable {
                            navigateToTeamProfile(
                                //TODO: Move this into processGame() in use case
                                if (game.homeTeam.city == "LA") "Los Angeles Clippers"
                                else if (game.homeTeam.name == "76ers") "Philadelphia Sixers"
                                else "${game.homeTeam.city} ${game.homeTeam.name}"
                            )
                        }
                )
                if (isGameUpcoming(game)) DisplayUpcomingInfo(game)
                else DisplayOngoingOrPreviousInfo(game)
                Image(
                    painter = painterResource(id = game.visitorTeam.logo),
                    contentDescription = "Away Team Logo",
                    modifier = Modifier
                        .size(50.dp)
                        //On click, nav to team profile, substituting e.g. Hawks with Atlanta Hawks
                        .clickable {
                            navigateToTeamProfile(
                                if (game.visitorTeam.city == "LA") "Los Angeles Clippers"
                                else if (game.visitorTeam.name == "76ers") "Philadelphia Sixers"
                                else "${game.visitorTeam.city} ${game.visitorTeam.name}"
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
                    text = game.homeTeam.name,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                // Spacer to create gap between home and away team names
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = game.visitorTeam.name,
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
        text = game.homeTeamScore.toString(),
        fontFamily = FontFamily.Serif,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
    Box {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (game.status == "Final" && game.homeTeamScore > game.visitorTeamScore) {
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
            if (game.status == "Final" && game.visitorTeamScore > game.homeTeamScore) {
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
        text = game.visitorTeamScore.toString(),
        fontFamily = FontFamily.Serif,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

private fun isGameUpcoming(game: Game): Boolean {
    return (game.status.contains("AM") || game.status.contains("PM"))
}
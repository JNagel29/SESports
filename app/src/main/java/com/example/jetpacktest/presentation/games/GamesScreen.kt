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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
                    NewGameCard(game, navigateToTeamProfile)
                }
            }
        }

    }
}
@Composable
fun NewGameCard(game: Game, navigateToTeamProfile: (String) -> Unit) {
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
                    painter = painterResource(id = game.homeTeam.logo),
                    contentDescription = "Home Team Logo",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {
                            navigateToTeamProfile(
                                if (game.homeTeam.city == "LA") "Los Angeles Clippers"
                                else if (game.homeTeam.name == "76ers") "Philadelphia Sixers"
                                else "${game.homeTeam.city} ${game.homeTeam.name}"
                            )
                        }
                )
                Text(
                    text = game.homeTeamScore.toString(),
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = game.visitorTeamScore.toString(),
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Image(
                    painter = painterResource(id = game.visitorTeam.logo),
                    contentDescription = "Away Team Logo",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {
                            navigateToTeamProfile(
                                if (game.visitorTeam.city == "LA") "Los Angeles Clippers"
                                else if (game.visitorTeam.name == "76ers") "Philadelphia Sixers"
                                else "${game.visitorTeam.city} ${game.visitorTeam.name}"
                            )
                        }
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
                    text = game.homeTeam.name,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = (if (game.time.isNullOrEmpty()) game.status else game.time)!!,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                )
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


/*

    val gamesHandler = GamesHandler()
    var selectedDate by rememberSaveable { mutableStateOf(Date()) }
    var gamesList by rememberSaveable { mutableStateOf<List<Game_Old>>(emptyList()) }
    var isFetching by remember { mutableStateOf(false)}
    var lastFetchedDate by remember { mutableStateOf(selectedDate) }

    LaunchedEffect(selectedDate) {
        if (gamesList.isEmpty() || selectedDate != lastFetchedDate) {
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
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(gamesList) { game ->
                    GameCard(gameOld = game, navigateToTeamProfile)
                }
            }
        }
        else CircularLoadingIcon()

    }


@Composable
fun GameCard(gameOld: Game_Old, navigateToTeamProfile: (String) -> Unit) {
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
                    painter = painterResource(id = gameOld.home_team.logo),
                    contentDescription = "Home Team Logo",
                    modifier = Modifier
                        .size(50.dp)
                        //On click, nav to team profile, substituting e.g. Hawks with Atlanta Hawks
                        .clickable {
                            navigateToTeamProfile(
                                NbaTeam.shortenedNamesToFullNames[gameOld.home_team.name]
                                    ?: gameOld.home_team.name
                            )
                        }
                )

                Text(
                    text = gameOld.home_team_score.toString(),
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = gameOld.visitor_team_score.toString(),
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Image(
                    painter = painterResource(id = gameOld.visitor_team.logo),
                    contentDescription = "Away Team Logo",
                    modifier = Modifier
                        .size(50.dp)
                        //On click, nav to team profile, substituting e.g. Hawks with Atlanta Hawks
                        .clickable {
                            navigateToTeamProfile(
                                NbaTeam.shortenedNamesToFullNames[gameOld.visitor_team.name]
                                    ?: gameOld.visitor_team.name
                            )
                        }
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
                    text = gameOld.home_team.name,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = (if (gameOld.time.isNullOrEmpty()) gameOld.status else gameOld.time)!!,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                )
                Text(
                    text = gameOld.visitor_team.name,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
} */
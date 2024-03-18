package com.example.jetpacktest.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpacktest.GamesHandler
import com.example.jetpacktest.models.Game
import java.util.Calendar


//TODO: Playing around with view model to see if data can persist b/w navbar screen change
class GamesViewModel: ViewModel() {
    var gamesList: List<Game> by mutableStateOf(emptyList())
}

@Composable
fun GamesScreen(gamesViewModel: GamesViewModel = viewModel()) {
    //Instantiate a games handler that we'll use to fetch daily games
    val gamesHandler = GamesHandler()
    //Also, instantiate empty list of games that we'll fetch on launch
    var gamesList by remember { mutableStateOf<List<Game>>(emptyList()) }
    //Context that we pass into fetchDailyGames for Volley
    val context = LocalContext.current

    //TESTING
    //var gamesList = gamesViewModel.gamesList

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

    //Lazy Column where each item is a game
    LazyColumn {
        items(gamesList) {game ->
            //Represent each game as a card (see below composable)
            GameCard(game = game)
        }
    }
}

@Composable
fun GameCard(game: Game) {
    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp // Adds shadow effect
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            //Top row will have names and score
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = game.homeName,
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp
                )
                Text(
                    text = game.homeScore,
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp
                )
                Text(
                    text = game.awayName,
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp
                )
                Text(
                    text = game.awayScore,
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            //Bottom row will have images and game time/status
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = game.gameTime,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp
                )
                Text(
                    text = game.gameStatus,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp
                )
            }
        }
    }
}
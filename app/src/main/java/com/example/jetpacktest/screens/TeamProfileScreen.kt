package com.example.jetpacktest.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.R
import com.example.jetpacktest.TeamHandler
import com.example.jetpacktest.models.NbaTeam

@Composable
fun TeamProfileScreen(teamName: String,
                      navigateBack: () -> Unit,
                      navigateToPlayerProfile: (String) -> Unit) {
    //Instantiate a team handler that we'll use to fetch players in a given team
    val teamHandler = TeamHandler()
    //Also, create a variable to hold our list of players
    //We need to use remember to ensure we re-compose whenever value changes (aka when we fetch)
    var teamPlayersList by remember { mutableStateOf<List<String>>(emptyList()) }
    //Context that we pass into fetchImageUrl for Volley
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        //Before anything else, fetch team abbreviation using dictionary in NbaTeam.kt
        val teamAbbrev = NbaTeam.namesToAbbreviations[teamName]
        //On first launch, fetch the list of teamPlayers and assign using our callback lambda
        if (teamAbbrev != null) {
            teamHandler.fetchTeamPlayers(teamAbbrev = teamAbbrev, context = context) { returnedPlayerList ->
                teamPlayersList = returnedPlayerList
            }
        }
        else Log.d("Team Profile Screen", "ERROR: namesToAbbreviations returned null")
    }
    //Wraps data inside column
    Column(modifier = Modifier.fillMaxSize()) {
        //Uses search header composable for back button from ProfileScreen.kt since same logic
        ReturnToSearchHeader(navigateBack)
        //Adds space between header and actual data
        Spacer(modifier = Modifier.height(15.dp))
        //Team name and logo (will pass into own composable w/ teamName, contentDesc, width/height
        //Wrap text in box to center it
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(text = teamName,
                fontSize = 26.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        //Add space between team name and logo
        Spacer(modifier = Modifier.height(8.dp))
        //We get the logo from NbaTeam.logos map (String to resource id)
        //The "?:" means if we get null, then just use default, else use left side
        val teamLogo = NbaTeam.logos[teamName] ?: R.drawable.fallback
        Image(
            painter = painterResource(id = teamLogo),
            contentDescription = teamName,
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
                .align(Alignment.CenterHorizontally)
        )
        //Adds space between image and list
        Spacer(modifier = Modifier.height(10.dp))
        //Display list of players using composable
        CurrentPlayerList(teamPlayersList, navigateToPlayerProfile)
    }
}
@Composable
fun CurrentPlayerList(teamPlayersList: List<String>,
                      navigateToPlayerProfile: (String) -> Unit) {
    //Header (we wrap inside box to center it)
    Box(modifier = Modifier.fillMaxWidth()) {
        Text("Current Players", fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
    //Slight amount of vertical space b/w header and list of names
    Spacer(modifier = Modifier.height(4.dp))
    //Now, we show list of player names
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()) {
        items(teamPlayersList) { playerName ->
            Text(text = playerName,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable {
                        //Navigate using lambda that routes profile screen
                        //Our playerName text includes jersey num and position with dashes, so trim
                        val trimmedPlayerName = playerName.substringBefore(" - ").trim()
                        navigateToPlayerProfile(trimmedPlayerName) // Navigate to that player's profile
                    })
        }
    }
}

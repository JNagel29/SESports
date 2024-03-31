package com.example.jetpacktest.screens

import ReturnToPreviousHeader
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.R
import com.example.jetpacktest.TeamHandler
import com.example.jetpacktest.TeamPlayer
import com.example.jetpacktest.models.NbaTeam

@Composable
fun TeamProfileScreen(teamName: String,
                      navigateBack: () -> Unit,
                      navigateToPlayerProfile: (String) -> Unit) {
    val teamHandler = TeamHandler()
    var teamPlayersList by remember { mutableStateOf<List<TeamPlayer>>(emptyList()) }

    LaunchedEffect(Unit) {
        //Before anything else, fetch team abbreviation using dictionary in NbaTeam.kt
        val teamAbbrev = NbaTeam.namesToAbbreviations[teamName]
        //On first launch, fetch the list of teamPlayers and assign using our callback lambda
        if (teamAbbrev != null) {
            teamHandler.fetchCurrentRoster(teamAbbrev = teamAbbrev) { result ->
                teamPlayersList = result
            }
            /*
            teamHandler.fetchTeamPlayers(teamAbbrev = teamAbbrev, context = context) { returnedPlayerList ->
                teamPlayersList = returnedPlayerList
            }
             */
        }
        else Log.d("Team Profile Screen", "ERROR: namesToAbbreviations returned null")
    }
    //Wraps data inside column
    Column(modifier = Modifier.fillMaxSize()) {
        //Uses search header composable for back button from ProfileScreen.kt since same logic
        ReturnToPreviousHeader(navigateBack)
        //Adds space between header and actual data
        Spacer(modifier = Modifier.height(15.dp))
        //Team name and logo (will pass into own composable w/ teamName, contentDesc, width/height
        //Wrap text in box to center it
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = teamName,
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
        CurrentRosterDisplay(teamPlayersList, navigateToPlayerProfile)
    }
}

@Composable
fun CurrentRosterDisplay(teamPlayersList: List<TeamPlayer>,
                      navigateToPlayerProfile: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Current Roster", //Header
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()) {
        itemsIndexed(teamPlayersList) { index, teamPlayer ->
            TeamPlayerRow(teamPlayer, navigateToPlayerProfile)
            if (index < teamPlayersList.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))
            }
        }
    }
}

@Composable
fun TeamPlayerRow(teamPlayer: TeamPlayer, navigateToPlayerProfile: (String) -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
        .clickable {
            val fullTeamPlayerName = "${teamPlayer.FirstName} ${teamPlayer.LastName}"
            navigateToPlayerProfile(fullTeamPlayerName)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "${teamPlayer.FirstName} ${teamPlayer.LastName}",
                fontSize = 22.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
            )
            Text(
                text = "${teamPlayer.Position} | #${teamPlayer.Jersey} | " +
                        "${inchesToFeet(teamPlayer.Height)} | ${teamPlayer.Weight} lbs | " +
                        teamPlayer.BirthCity,
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
            )
        }
    }
}

private fun inchesToFeet(inches: Int): String {
    val feet = inches / 12
    val remainingInches = inches % 12
    return "$feet'$remainingInches\""
}
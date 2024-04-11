package com.example.jetpacktest.presentation.team

import ReturnToPreviousHeader
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jetpacktest.R
import com.example.jetpacktest.common.components.CircularLoadingIcon
import com.example.jetpacktest.domain.model.TeamPlayer
import com.example.jetpacktest.models.NbaTeam

@Composable
fun TeamProfileScreen(
    navigateBack: () -> Unit,
    navigateToPlayerProfile: (String) -> Unit,
    viewModel: TeamProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    if (state.teamPlayers.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ReturnToPreviousHeader(navigateBack)
            TeamNameHeader(state.teamName)
            Spacer(modifier = Modifier.height(8.dp))
            TeamLogo(state.teamName)
            CurrentRosterDisplay(state.teamPlayers, navigateToPlayerProfile)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
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
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularLoadingIcon()
        }
    }
}

@Composable
fun TeamNameHeader(teamName: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = teamName,
            fontSize = 26.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ColumnScope.TeamLogo(teamName: String) {
    val teamLogo = NbaTeam.logos[teamName] ?: R.drawable.fallback
    Image(
        painter = painterResource(id = teamLogo),
        contentDescription = teamName,
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .align(Alignment.CenterHorizontally)
    )
}

@Composable
fun CurrentRosterDisplay(teamPlayersList: List<TeamPlayer>,
                         navigateToPlayerProfile: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Current Roster",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.align(Alignment.Center)
        )
    }
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize())
    {
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
            val fullTeamPlayerName = "${teamPlayer.firstName} ${teamPlayer.lastName}"
            navigateToPlayerProfile(fullTeamPlayerName)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "${teamPlayer.firstName} ${teamPlayer.lastName}",
                fontSize = 22.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
            )
            Text(
                text = "${teamPlayer.position} | #${teamPlayer.jersey} | " +
                        "${inchesToFeet(teamPlayer.height)} | ${teamPlayer.weight} lbs | " +
                        teamPlayer.birthCity,
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
            )
        }
    }
}

private fun inchesToFeet(inches: Int?): String {
    if (inches == null) return ""
    val feet = inches / 12
    val remainingInches = inches % 12
    return "$feet'$remainingInches\""
}
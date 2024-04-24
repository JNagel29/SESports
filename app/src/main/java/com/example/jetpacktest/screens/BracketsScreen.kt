package com.example.jetpacktest.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataSaverOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.jetpacktest.R
import com.example.jetpacktest.ui.components.ReturnToPreviousHeader

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BracketsScreen(navigateBack: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        stickyHeader {
            ReturnToPreviousHeader(
                navigateBack = navigateBack,
                label = "Standings Screen"
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            LazyRow {
                //Column for each round
                item { FirstRoundColumn() }
                item { Spacer(modifier = Modifier.width(20.dp)) }
                item { SecondRoundColumn() }
                item { Spacer(modifier = Modifier.width(20.dp)) }
                item { ThirdRoundColumn() }
                item { Spacer(modifier = Modifier.width(20.dp)) }
                item { FourthRoundColumn() }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun FirstRoundColumn() {
    Column {
        val firstRoundMatchups = listOf(
            Pair("OKC   1", "NOLA  0") to Pair(R.drawable.xml_thunder, R.drawable.xml_pelicans),
            Pair("LAL   0", "DEN   2") to Pair(R.drawable.xml_lakers, R.drawable.xml_nuggets),
            Pair("MIN   1", "PHX   0") to Pair(R.drawable.xml_timberwolves, R.drawable.xml_suns),
            Pair("LAC   1", "DAL   0") to Pair(R.drawable.xml_clippers, R.drawable.xml_mavericks),
            Pair("BOS   1", "MIA   0") to Pair(R.drawable.xml_celtics, R.drawable.xml_heat),
            Pair("NYK   2", "PHI   0") to Pair(R.drawable.xml_knicks, R.drawable.xml_sixers),
            Pair("MIL   1", "IND   0") to Pair(R.drawable.xml_bucks, R.drawable.xml_pacers),
            Pair("CLE   2", "ORL   0") to Pair(R.drawable.xml_cavaliers, R.drawable.xml_magic)
        )

        firstRoundMatchups.forEach { (teams, logos) ->
            BracketMatchup(teams.first, teams.second, logos.first, logos.second, tbd = false)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SecondRoundColumn() {
    Column {
        Spacer(modifier = Modifier.height(70.dp))

        val secondRoundMatchups = listOf(
            Pair("Winner   1", "Winner   2") to Pair(R.drawable.xml_thunder, R.drawable.xml_lakers),
            Pair("Winner   3", "Winner   4") to Pair(R.drawable.xml_timberwolves, R.drawable.xml_mavericks),
            Pair("Winner   5", "Winner   6") to Pair(R.drawable.xml_celtics, R.drawable.xml_knicks),
            Pair("Winner   7", "Winner   8") to Pair(R.drawable.xml_cavaliers, R.drawable.xml_bucks)
        )

        secondRoundMatchups.forEachIndexed { index, (teams, logos) ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(150.dp))
            }
            BracketMatchup(teams.first, teams.second, logos.first, logos.second, tbd = true)
        }
    }
}

@Composable
private fun ThirdRoundColumn() {
    Column {
        Spacer(modifier = Modifier.height(215.dp))

        val thirdRoundMatchups = listOf(
            Pair("Winner   1", "Winner   3") to Pair(R.drawable.xml_thunder, R.drawable.xml_timberwolves),
            Pair("Winner   5", "Winner   7") to Pair(R.drawable.xml_celtics, R.drawable.xml_cavaliers),
        )

        thirdRoundMatchups.forEachIndexed { index, (teams, logos) ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(415.dp))
            }
            BracketMatchup(teams.first, teams.second, logos.first, logos.second, tbd = true)
        }
    }
}

@Composable
private fun FourthRoundColumn() {
    Column {
        Spacer(modifier = Modifier.height(450.dp))

        val fourthRoundMatchups = listOf(
            Pair("Winner   1", "Winner   5") to Pair(R.drawable.xml_thunder, R.drawable.xml_celtics),
        )

        fourthRoundMatchups.forEach { (teams, logos) ->
            BracketMatchup(teams.first, teams.second, logos.first, logos.second, tbd = true)
        }
    }
}

@Composable
fun BracketMatchup(
    teamAName: String, teamBName: String, teamALogo: Int, teamBLogo: Int, tbd: Boolean
) {
    Card(
        modifier = Modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            contentColor = Color.Black
        )
    ) {
        Column {
            BracketRow(teamName = teamAName, logoResId = teamALogo, tbd = tbd)
            Spacer(modifier = Modifier.height(10.dp))
            BracketRow(teamName = teamBName, logoResId = teamBLogo, tbd = tbd)
        }
    }
}

@Composable
fun BracketRow(teamName: String, logoResId: Int, tbd: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        if (tbd) {
            Icon(
                imageVector = Icons.Default.DataSaverOff,
                contentDescription = null
            )
        }
        else {
            Image(
                painter = painterResource(id = logoResId),
                contentDescription = "Team Logo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = teamName)
    }
}


/*
@Composable
fun BracketLine() {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(16.dp)) {
        drawLine(
            color = Color.Black,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
 */
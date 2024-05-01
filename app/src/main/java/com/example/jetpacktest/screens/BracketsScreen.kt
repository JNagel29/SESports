package com.example.jetpacktest.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataSaverOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.R
import kotlinx.coroutines.launch

data class Matchup (
    val team1: TeamBracket,
    val team2: TeamBracket
)

data class TeamBracket (
    val abbrev: String,
    val gamesWon: Int?,
    val logo: Int?,
    val standing: Int?
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BracketsScreen() {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val titles = listOf("1st Round", "Conf. Semifinals", "Conf. Finals", "NBA Finals")
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    height = 3.dp,
                    color = Color.Black
                )
            }
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    text = {
                        Text(
                            text = title,
                            color = Color.Black,
                            fontFamily = FontFamily.Serif,
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page = index)
                        }
                    }
                )
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            HorizontalPager(
                state = pagerState
            ) { page ->
                when (page) {
                    //Only need LazyColumn on first two b/c many match-ups
                    0 -> LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        item { FirstRoundColumn() }
                    }
                    1 -> LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        item { SecondRoundColumn() }
                    }
                    2 -> ThirdRoundColumn()
                    3 -> FourthRoundColumn()
                }
            }
        }
    }
}
@Composable
private fun FirstRoundColumn() {
    Column {
        Spacer(modifier = Modifier.height(10.dp))

        val firstRoundMatchups = listOf(
            Matchup(
                TeamBracket(abbrev = "Celtics", gamesWon = 3, logo = R.drawable.xml_celtics, standing = 1),
                TeamBracket(abbrev = "Heat", gamesWon = 1, logo = R.drawable.xml_heat, standing = 8)
            ),
            Matchup(
                TeamBracket(abbrev = "Cavaliers", gamesWon = 2, logo = R.drawable.xml_cavaliers, standing = 4),
                TeamBracket(abbrev = "Magic", gamesWon = 2, logo = R.drawable.xml_magic, standing = 5)
            ),
            Matchup(
                TeamBracket(abbrev = "Bucks", gamesWon = 1, logo = R.drawable.xml_bucks, standing = 3),
                TeamBracket(abbrev = "Pacers", gamesWon = 3, logo = R.drawable.xml_pacers, standing = 6)
            ),
            Matchup(
                TeamBracket(abbrev = "Knicks", gamesWon = 3, logo = R.drawable.xml_knicks, standing = 2),
                TeamBracket(abbrev = "76ers", gamesWon = 1, logo = R.drawable.xml_sixers, standing = 7)
            ),
            Matchup(
                TeamBracket(abbrev = "Thunder", gamesWon = 4, logo = R.drawable.xml_thunder, standing = 1),
                TeamBracket(abbrev = "Pelicans", gamesWon = 0, logo = R.drawable.xml_pelicans, standing = 8)
            ),
            Matchup(
                TeamBracket(abbrev = "Clippers", gamesWon = 2, logo = R.drawable.xml_clippers, standing = 4),
                TeamBracket(abbrev = "Mavericks", gamesWon = 2, logo = R.drawable.xml_mavericks, standing = 5)
            ),
            Matchup(
                TeamBracket(abbrev = "Timberwolves", gamesWon = 4, logo = R.drawable.xml_timberwolves, standing = 3),
                TeamBracket(abbrev = "Suns", gamesWon = 0, logo = R.drawable.xml_suns, standing = 6 )
            ),
            Matchup(
                TeamBracket(abbrev = "Nuggets", gamesWon = 4, logo = R.drawable.xml_nuggets, standing = 2),
                TeamBracket(abbrev = "Lakers", gamesWon = 1, logo = R.drawable.xml_lakers, standing = 7)
            )
        )

        firstRoundMatchups.forEachIndexed { index, matchUp ->
            if (index == 0)
                ConferenceNameHeader(conferenceName = "EAST")
            else if (index == 4)
                ConferenceNameHeader(conferenceName = "WEST")
            MatchupCard(team1 = matchUp.team1, team2 = matchUp.team2)
        }
    }
}

@Composable
private fun SecondRoundColumn() {
    Column {
        Spacer(modifier = Modifier.height(10.dp))

        val secondRoundMatchups = listOf(
            Matchup(
                TeamBracket(abbrev = "TBD", gamesWon = null, logo = null, standing = null),
                TeamBracket(abbrev = "TBD", gamesWon = null, logo = null, standing = null)
            ),
            Matchup(
                TeamBracket(abbrev = "TBD", gamesWon = null, logo = null, standing = null),
                TeamBracket(abbrev = "TBD", gamesWon = null, logo = null, standing = null)
            ),
            Matchup(
                TeamBracket(abbrev = "Thunder", gamesWon = null, logo = R.drawable.xml_thunder, standing = 1),
                TeamBracket(abbrev = "TBD", gamesWon = null, logo = null, standing = null)
            ),
            Matchup(
                TeamBracket(abbrev = "Timberwolves", gamesWon = null, logo = R.drawable.xml_timberwolves, standing = 3),
                TeamBracket(abbrev = "Nuggets", gamesWon = null, logo = R.drawable.xml_nuggets, standing = 2),
            )
        )
        secondRoundMatchups.forEachIndexed { index, matchUp ->
            if (index == 0)
                ConferenceNameHeader(conferenceName = "EAST")
            else if (index == 2)
                ConferenceNameHeader(conferenceName = "WEST")
            MatchupCard(team1 = matchUp.team1, team2 = matchUp.team2)
        }
    }
}

@Composable
private fun ThirdRoundColumn() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Spacer(modifier = Modifier.height(10.dp))

            val thirdRoundMatchups = listOf(
                Matchup(
                    TeamBracket(abbrev = "TBD", gamesWon = null, logo = null, standing = null),
                    TeamBracket(abbrev = "TBD", gamesWon = null, logo = null, standing = null)
                ),
                Matchup(
                    TeamBracket(abbrev = "TBD", gamesWon = null, logo = null, standing = null),
                    TeamBracket(abbrev = "TBD", gamesWon = null, logo = null, standing = null)
                )
            )

            thirdRoundMatchups.forEachIndexed { index, matchUp ->
                if (index == 0)
                    ConferenceNameHeader(conferenceName = "EAST")
                else if (index == 1)
                    ConferenceNameHeader(conferenceName = "WEST")
                MatchupCard(team1 = matchUp.team1, team2 = matchUp.team2)
            }
        }
    }
}

@Composable
private fun FourthRoundColumn() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Spacer(modifier = Modifier.height(10.dp))

            val fourthRoundMatchup = Matchup(
                TeamBracket(abbrev = "TBD", gamesWon = null, logo = null, standing = null),
                TeamBracket(abbrev = "TBD", gamesWon = null, logo = null, standing = null)
            )

            ConferenceNameHeader(conferenceName = "NBA FINALS")
            MatchupCard(team1 = fourthRoundMatchup.team1, team2 = fourthRoundMatchup.team2)
        }
    }
}

@Composable
fun MatchupCard(team1: TeamBracket, team2: TeamBracket) {
    Card(
        modifier = Modifier.padding(16.dp).width(250.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            contentColor = Color.Black
        )
    ) {
        Column {
            TeamRow(
                name = team1.abbrev,
                gamesWon = team1.gamesWon,
                logo = team1.logo,
                standing = team1.standing,
                isEliminated = team2.gamesWon == 4 // Eliminated iff other team has 4 wins
            )
            HorizontalDivider(
                color = Color.LightGray,
                modifier = Modifier.padding(vertical = 5.dp)
            )
            TeamRow(
                name = team2.abbrev,
                gamesWon = team2.gamesWon,
                logo = team2.logo,
                standing = team2.standing,
                isEliminated = team1.gamesWon == 4
            )
        }
    }
}

@Composable
fun ConferenceNameHeader(conferenceName: String) {
    Text(
        text = conferenceName,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun TeamRow(name: String, gamesWon: Int?, logo: Int?, standing: Int?, isEliminated: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .alpha(if (!isEliminated) 1.0f else .5f) // Gray out if eliminated
    ) {
        if (logo == null) {
            Icon(
                imageVector = Icons.Default.DataSaverOff,
                contentDescription = null,
                modifier = Modifier.size(35.dp)
            )
        }
        else {
            Image(
                painter = painterResource(id = logo),
                contentDescription = "Team Logo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        standing?.let {
            Text(
                text = standing.toString(),
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name)
        Spacer(modifier = Modifier.width(12.dp))
        gamesWon?.let {
            Text(text = gamesWon.toString())
        }
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
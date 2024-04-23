package com.example.jetpacktest.screens
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpacktest.R

@Preview
@Composable
fun StandingsScreen() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp)) // Top padding
        Row {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // Top padding
                BracketMatchup("OKC    1", "NOLA  0", R.drawable.thunder, R.drawable.pelicans)
                Spacer(modifier = Modifier.height(16.dp)) // Spacing between matchups
                BracketMatchup("LAL     0", "DEN    2", R.drawable.lakers, R.drawable.nuggets)
                Spacer(modifier = Modifier.height(16.dp)) // Spacing between matchups
                BracketMatchup("MIN    1", "PHX   0", R.drawable.xml_timberwolves, R.drawable.suns)
                Spacer(modifier = Modifier.height(16.dp)) // Spacing between matchups
                BracketMatchup(
                    "LAC    1",
                    "DAL    0",
                    R.drawable.clippers,
                    R.drawable.xml_mavericks
                )
                Spacer(modifier = Modifier.height(16.dp)) // Spacing between matchups
                BracketMatchup("BOS     1", "MIA     0", R.drawable.celtics, R.drawable.heat)
                Spacer(modifier = Modifier.height(16.dp)) // Spacing between matchups
                BracketMatchup("NYK     2", "PHI      0", R.drawable.knicks, R.drawable.sixers)
                Spacer(modifier = Modifier.height(16.dp)) // Spacing between matchups
                BracketMatchup("MIL     1", "IND     0", R.drawable.bucks, R.drawable.pacers)
                Spacer(modifier = Modifier.height(16.dp)) // Spacing between matchups
                BracketMatchup("CLE    2", "ORL    0", R.drawable.cavs, R.drawable.magic)
                Spacer(modifier = Modifier.height(16.dp)) // Spacing between matchups
            }
            Column(

                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.height(85.dp))
                BracketMatchup(
                    "Winner 1",
                    "Winner 2",
                    R.drawable.thunder,
                    R.drawable.lakers
                )
                Spacer(modifier = Modifier.height(150.dp)) // Spacing between matchups
                BracketMatchup(
                    "Winner 3",
                    "Winner 4",
                    R.drawable.xml_timberwolves,
                    R.drawable.mavs
                )
                Spacer(modifier = Modifier.height(150.dp)) // Spacing between matchups
                BracketMatchup(
                    "Winner 5",
                    "Winner 6",
                    R.drawable.celtics,
                    R.drawable.knicks
                )
                Spacer(modifier = Modifier.height(135.dp)) // Spacing between matchups
                BracketMatchup(
                    "Winner 7",
                    "Winner 8",
                    R.drawable.cavs,
                    R.drawable.bucks
                )
                // Add more second round matchups here
            }

        }
        Spacer(modifier = Modifier.height(16.dp)) // Bottom padding
    }
}
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



@Composable
fun BracketMatchup(teamAName: String, teamBName: String, teamALogo: Int, teamBLogo: Int) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            BracketNodeCard(teamAName, teamALogo)
        }
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            BracketNodeCard(teamBName, teamBLogo)
        }
        Spacer(modifier = Modifier.height(16.dp)) // Spacing between matchups
    }
}

@Composable
fun BracketNodeCard(teamName: String, logoResId: Int) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .height(50.dp)
            .background(Color.DarkGray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = logoResId),
                contentDescription = "Team Logo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(8.dp)) // Spacing between logo and text
            Text(
                text = teamName,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}





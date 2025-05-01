import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.models.StatLeader
import com.example.jetpacktest.screens.StatLeaderCard
import com.example.jetpacktest.ui.components.LargeDropdownMenu
import kotlinx.coroutines.flow.Flow

@Composable
fun BaseballHomeScreen(
    randomStat: String, // unused but passed
    chosenStatFlow: Flow<String>,
    chosenYearFlow: Flow<String>,
    statLeadersListFlow: Flow<List<StatLeader>>,
    fetchStatLeaders: (isPitcher: Boolean, chosenStat: String, chosenYear: String) -> Unit,
    updateChosenStat: (String) -> Unit,
    updateChosenYear: (String) -> Unit,
    navigateToPlayerProfile: (String) -> Unit
) {
    val yearOptions = listOf("2020", "2021", "2022", "2023", "2024")

    val batterStatOptions = listOf(
        "air_outs", "at_bats", "at_bats_per_home_run", "avg", "babip", "base_on_balls",
        "catchers_interference", "caught_stealing", "doubles", "games_played",
        "ground_into_double_play", "ground_outs", "ground_outs_to_airouts", "hit_by_pitch", "hits",
        "home_runs", "intentional_walks", "left_on_base", "number_of_pitches", "obp", "ops",
        "plate_appearances", "rbi", "runs", "sac_bunts", "sac_flies", "slg",
        "stolen_base_percentage", "stolen_bases", "strike_outs", "total_bases", "triples"
    )

    val batterStatLabels = mapOf(
        "air_outs" to "Air Outs", "at_bats" to "At Bats", "at_bats_per_home_run" to "AB per HR",
        "avg" to "Batting Average", "babip" to "BABIP", "base_on_balls" to "Walks (BB)",
        "catchers_interference" to "Catcher Interference", "caught_stealing" to "Caught Stealing",
        "doubles" to "Doubles", "games_played" to "Games Played",
        "ground_into_double_play" to "GIDP", "ground_outs" to "Ground Outs",
        "ground_outs_to_airouts" to "GO/AO", "hit_by_pitch" to "Hit by Pitch", "hits" to "Hits",
        "home_runs" to "Home Runs", "intentional_walks" to "Intentional Walks",
        "left_on_base" to "Left on Base", "number_of_pitches" to "Pitches", "obp" to "OBP",
        "ops" to "OPS", "plate_appearances" to "Plate Appearances", "rbi" to "RBIs",
        "runs" to "Runs", "sac_bunts" to "Sac Bunts", "sac_flies" to "Sac Flies",
        "slg" to "Slugging %", "stolen_base_percentage" to "SB %", "stolen_bases" to "Stolen Bases",
        "strike_outs" to "Strikeouts", "total_bases" to "Total Bases", "triples" to "Triples"
    )

    val pitcherStatOptions = listOf(
        "gamesPitched", "gamesStarted", "wins", "losses", "era", "inningsPitched",
        "strikeOuts", "walks", "whip", "saves", "saveOpportunities", "blownSaves", "holds",
        "hitsAllowed", "homeRunsAllowed", "earnedRuns", "strikeoutsPer9Inn", "walksPer9Inn",
        "homeRunsPer9", "inheritedRunners", "inningsPitched", "wildPitches", "pickoffs",
        "battersFaced", "pitchesPerInning", "runs", "runsScoredPer9"
    )

    val pitcherStatLabels = pitcherStatOptions.associateWith {
        it.replace(Regex("(?<=[a-z])(?=[A-Z])"), " ").replaceFirstChar(Char::uppercaseChar)
    }

    var showingPitchers by remember { mutableStateOf(false) }

    val statLeadersList by statLeadersListFlow.collectAsState(initial = emptyList())
    val chosenStat by chosenStatFlow.collectAsState(initial = if (showingPitchers) "era" else "home_runs")
    val chosenYear by chosenYearFlow.collectAsState(initial = yearOptions.first())

    val statOptions = if (showingPitchers) pitcherStatOptions else batterStatOptions
    val statLabels = if (showingPitchers) pitcherStatLabels else batterStatLabels

    LaunchedEffect(showingPitchers) {
        fetchStatLeaders(showingPitchers, chosenStat, chosenYear)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Button(
            onClick = {
                showingPitchers = !showingPitchers
                val defaultStat = if (showingPitchers) "era" else "home_runs"
                updateChosenStat(defaultStat)
                fetchStatLeaders(showingPitchers, defaultStat, chosenYear)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (showingPitchers) "Switch to Batters" else "Switch to Pitchers",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (showingPitchers) "Pitcher Stat Leaders" else "Batter Stat Leaders",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth()) {
            LargeDropdownMenu(
                label = "Select Stat:",
                items = statOptions.map { statLabels[it] ?: it },
                selectedIndex = statOptions.indexOf(chosenStat).coerceAtLeast(0),
                onItemSelected = { index, _ ->
                    val stat = statOptions[index]
                    updateChosenStat(stat)
                    fetchStatLeaders(showingPitchers, stat, chosenYear)
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            LargeDropdownMenu(
                label = "Select Year:",
                items = yearOptions,
                selectedIndex = yearOptions.indexOf(chosenYear),
                onItemSelected = { index, _ ->
                    val year = yearOptions[index]
                    updateChosenYear(year)
                    fetchStatLeaders(showingPitchers, chosenStat, year)
                },
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(statLeadersList) { statLeader ->
                StatLeaderCard(
                    statLeader = statLeader,
                    chosenStat = statLabels[chosenStat] ?: chosenStat,
                    navigateToPlayerProfile = navigateToPlayerProfile
                )
            }
        }
    }
}

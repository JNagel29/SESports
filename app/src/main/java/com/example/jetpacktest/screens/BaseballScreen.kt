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
    randomStat: String, // still here but unused
    chosenStatFlow: Flow<String>,
    chosenYearFlow: Flow<String>,
    statLeadersListFlow: Flow<List<StatLeader>>,
    fetchStatLeaders: (isPitcher: Boolean, chosenStat: String, chosenYear: String) -> Unit,
    updateChosenStat: (String) -> Unit,
    updateChosenYear: (String) -> Unit,
    navigateToPlayerProfile: (String) -> Unit
) {
    val yearOptions = listOf("2020", "2021", "2022", "2023", "2024")

    // Key -> Label mappings
    val batterStatMap = mapOf(
        "air_outs" to "Air Outs",
        "at_bats" to "At Bats",
        "at_bats_per_home_run" to "AB/HR",
        "avg" to "Batting Average",
        "babip" to "BABIP",
        "base_on_balls" to "Walks (BB)",
        "catchers_interference" to "Catcher's Interference",
        "caught_stealing" to "Caught Stealing",
        "doubles" to "Doubles",
        "games_played" to "Games Played",
        "ground_into_double_play" to "GIDP",
        "ground_outs" to "Ground Outs",
        "ground_outs_to_airouts" to "GO/AO",
        "hit_by_pitch" to "Hit By Pitch",
        "hits" to "Hits",
        "home_runs" to "Home Runs",
        "intentional_walks" to "Intentional Walks",
        "left_on_base" to "Left On Base",
        "number_of_pitches" to "Pitches",
        "obp" to "On-Base %",
        "ops" to "OPS",
        "plate_appearances" to "Plate Appearances",
        "rbi" to "RBIs",
        "runs" to "Runs",
        "sac_bunts" to "Sac Bunts",
        "sac_flies" to "Sac Flies",
        "slg" to "Slugging %",
        "stolen_base_percentage" to "SB %",
        "stolen_bases" to "Stolen Bases",
        "strike_outs" to "Strikeouts",
        "total_bases" to "Total Bases",
        "triples" to "Triples"
    )

    val pitcherStatMap = mapOf(
        "games_pitched" to "Games Pitched",
        "games_started" to "Games Started",
        "wins" to "Wins",
        "losses" to "Losses",
        "era" to "ERA",
        "innings_pitched" to "Innings Pitched",
        "strike_outs" to "Strikeouts",
        "walks" to "Walks",
        "whip" to "WHIP",
        "saves" to "Saves",
        "save_opportunities" to "Save Ops",
        "blown_saves" to "Blown Saves",
        "holds" to "Holds",
        "hits_allowed" to "Hits Allowed",
        "home_runs_allowed" to "HR Allowed",
        "earned_runs" to "Earned Runs",
        "strikeouts_per9inn" to "K/9",
        "walks_per9inn" to "BB/9",
        "home_runs_per9" to "HR/9",
        "inherited_runners" to "Inherited Runners",
        "wild_pitches" to "Wild Pitches",
        "pickoffs" to "Pickoffs",
        "batters_faced" to "Batters Faced",
        "pitches_per_inning" to "Pitches/Inning",
        "runs" to "Runs Allowed",
        "runs_scored_per9" to "Runs/9"
    )

    var showingPitchers by remember { mutableStateOf(false) }
    val statLeadersList by statLeadersListFlow.collectAsState(initial = emptyList())
    // Track chosen keys
    val chosenStatKey by chosenStatFlow.collectAsState(initial = if (showingPitchers) "era" else "home_runs")
    val chosenYear by chosenYearFlow.collectAsState(initial = yearOptions.first())

    // Build lists of keys and labels
    val batterKeys = batterStatMap.keys.toList()
    val batterLabels = batterKeys.map { batterStatMap[it] ?: it }
    val pitcherKeys = pitcherStatMap.keys.toList()
    val pitcherLabels = pitcherKeys.map { pitcherStatMap[it] ?: it }

    LaunchedEffect(showingPitchers) {
        // On toggling category, fetch with current key
        fetchStatLeaders(showingPitchers, chosenStatKey, chosenYear)
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
                // Default to first key of new category
                val defaultKey = if (showingPitchers) pitcherKeys.first() else batterKeys.first()
                updateChosenStat(defaultKey)
                fetchStatLeaders(showingPitchers, defaultKey, chosenYear)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (showingPitchers) "Switch to Batters" else "Switch to Pitchers",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = if (showingPitchers) "Pitcher Stat Leaders" else "Batter Stat Leaders",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth()) {
            LargeDropdownMenu(
                label = "Stat",
                items = if (showingPitchers) pitcherLabels else batterLabels,
                selectedIndex = if (showingPitchers)
                    pitcherKeys.indexOf(chosenStatKey).coerceAtLeast(0)
                else
                    batterKeys.indexOf(chosenStatKey).coerceAtLeast(0),
                onItemSelected = { index, _ ->
                    val key = if (showingPitchers) pitcherKeys[index] else batterKeys[index]
                    updateChosenStat(key)
                    fetchStatLeaders(showingPitchers, key, chosenYear)
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            LargeDropdownMenu(
                label = "Year",
                items = yearOptions,
                selectedIndex = yearOptions.indexOf(chosenYear),
                onItemSelected = { index, _ ->
                    val year = yearOptions[index]
                    updateChosenYear(year)
                    fetchStatLeaders(showingPitchers, chosenStatKey, year)
                },
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(Modifier.padding(top = 16.dp)) {
            items(statLeadersList) { statLeader ->
                StatLeaderCard(
                    statLeader = statLeader,
                    chosenStat = chosenStatKey,
                    navigateToPlayerProfile = navigateToPlayerProfile
                )
            }
        }
    }
}

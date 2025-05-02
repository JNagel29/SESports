package com.example.jetpacktest.propsBaseball.sportradar

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpacktest.propsBaseball.model.BaseballEvent
import org.json.JSONObject

@Composable
fun SportradarScheduleScreen(viewModel: SportradarViewModel = viewModel()) {
    val context = LocalContext.current
    val today = remember { java.time.LocalDate.now().toString() }

    // Read state directly from the ViewModel's mutableStateOf
    val scheduleJson = viewModel.scheduleJson.value
    val error = viewModel.error.value

    LaunchedEffect(Unit) {
        viewModel.loadSchedule(today)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("MLB Schedule - $today", style = MaterialTheme.typography.headlineSmall)

        if (!error.isNullOrEmpty()) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }

        if (!scheduleJson.isNullOrEmpty()) {
            val events = parseSchedule(scheduleJson)
            LazyColumn {
                items(events) { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                val intent = Intent(context, SportradarPlayerPropsActivity::class.java).apply {
                                    putExtra("event_id", event.id)
                                    putExtra("home_team", event.homeTeam)
                                    putExtra("away_team", event.awayTeam)
                                }
                                context.startActivity(intent)
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${event.awayTeam} @ ${event.homeTeam}")
                            Text("Time: ${event.commenceTime}")
                        }
                    }
                }
            }
        } else {
            Spacer(Modifier.height(16.dp))
            Text("Loading games...")
        }
    }
}

fun parseSchedule(json: String): List<BaseballEvent> {
    val list = mutableListOf<BaseballEvent>()
    try {
        val root = JSONObject(json)
        val events = root.getJSONArray("sport_events")
        for (i in 0 until events.length()) {
            val obj = events.getJSONObject(i)
            val id = obj.getString("id")
            val startTime = obj.optString("start_time", "N/A")

            val competitors = obj.getJSONArray("competitors")
            var home = ""
            var away = ""

            for (j in 0 until competitors.length()) {
                val team = competitors.getJSONObject(j)
                val role = team.optString("qualifier")
                val name = team.optString("name")

                if (role == "home") home = name
                if (role == "away") away = name
            }

            list.add(BaseballEvent(id, home, away, startTime))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return list
}

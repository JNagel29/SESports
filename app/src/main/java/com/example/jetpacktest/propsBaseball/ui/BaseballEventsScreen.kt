package com.example.jetpacktest.propsBaseball.ui

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpacktest.propsBaseball.model.BaseballEvent
import com.example.jetpacktest.propsBaseball.sportradar.SportradarPlayerPropsActivity
import com.example.jetpacktest.propsBaseball.viewmodel.BaseballEventsViewModel

@Composable
fun BaseballEventsScreen(viewModel: BaseballEventsViewModel = viewModel()) {
    val context = LocalContext.current
    val events = viewModel.events.value
    val error = viewModel.error.value

    LaunchedEffect(Unit) {
        viewModel.fetchEvents()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Upcoming Baseball Events",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn {
            items(events) { event ->
                BaseballEventItem(event)
            }
        }

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun BaseballEventItem(event: BaseballEvent) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                if (event.id == "static_world_series") {
                    val intent = Intent(context, StaticBaseballPropsActivity::class.java)
                    context.startActivity(intent)
                } else {
                    val intent = Intent(context, SportradarPlayerPropsActivity::class.java).apply {
                        putExtra("event_id", event.id)
                        putExtra("home_team", event.homeTeam)
                        putExtra("away_team", event.awayTeam)
                    }
                    context.startActivity(intent)
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${event.awayTeam} @ ${event.homeTeam}", style = MaterialTheme.typography.titleMedium)
            Text("Time: ${event.commenceTime}")
        }
    }
}

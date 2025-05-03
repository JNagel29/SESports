// app/src/main/java/com/example/sportify2/ui2/EventsScreen.kt
package com.example.jetpacktest.props.ui2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jetpacktest.props.network.models.Event
import com.example.jetpacktest.props.viewmodel.EventsUiState

@Composable
fun EventsScreen(
    uiState: EventsUiState,
    onSelectEvent: (String) -> Unit,
    onRefresh: () -> Unit
) {
    when (uiState) {
        EventsUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is EventsUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: ${uiState.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onRefresh) {
                        Text("Retry")
                    }
                }
            }
        }
        is EventsUiState.Success -> {
            val events: List<Event> = uiState.events

            Column(Modifier.fillMaxSize()) {
                // ─── App header above matchups ────────────────────────
                Text(
                    text = "Betting Research Tool",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    maxLines = 1
                )

                // ─── List of games ──────────────────────────────────
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(events) { event ->
                        EventRow(
                            event = event,
                            onClick = { onSelectEvent(event.id.toString()) }
                        )
                        Divider()
                    }
                }

                // ─── Refresh button ─────────────────────────────────
                Button(
                    onClick = onRefresh,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Refresh")
                }
            }
        }
    }
}

@Composable
private fun EventRow(event: Event, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Text(
            text = "${event.homeTeam} vs ${event.awayTeam}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Starts: ${event.commenceTime}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

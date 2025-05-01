package com.example.jetpacktest.propsBaseball.sportradar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpacktest.props.util.formatAmerican

@Composable
fun SportradarPropsScreen(
    eventId: String,
    homeTeam: String? = null,
    awayTeam: String? = null,
    viewModel: SportradarPropsViewModel = viewModel()
) {
    val props by viewModel.props.collectAsState()
    val error by viewModel.error.collectAsState()
    val displayedBookies = listOf("DraftKings", "FanDuel", "BetMGM")

    val statOptions = props.map { it.stat }.distinct()
    var selectedStat by remember { mutableStateOf(statOptions.firstOrNull() ?: "") }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(eventId, selectedStat) {
        if (selectedStat.isNotEmpty()) {
            viewModel.fetchPlayerProps(eventId, selectedStat)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("MLB Player Props", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))

        // Dropdown to select stat
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (selectedStat.isNotEmpty()) selectedStat else "Select Stat")
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            statOptions.forEach { stat ->
                DropdownMenuItem(
                    text = { Text(stat) },
                    onClick = {
                        selectedStat = stat
                        expanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (error.isNotEmpty()) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else {
            val filtered = props.filter { it.stat == selectedStat }
                .groupBy { it.playerName }

            // Table Header
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            ) {
                Text("Player", Modifier.width(100.dp), style = MaterialTheme.typography.titleSmall)
                displayedBookies.forEach { bk ->
                    Text(
                        text = bk,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Divider()

            LazyColumn(Modifier.weight(1f)) {
                items(filtered.entries.toList()) { (player, entries) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(player, Modifier.width(100.dp), style = MaterialTheme.typography.bodyMedium)

                        displayedBookies.forEach { bk ->
                            val match = entries.firstOrNull { prop ->
                                prop.outcomes.any { it.sportsbook.equals(bk, ignoreCase = true) }
                            }
                            val outcome = match?.outcomes?.firstOrNull { it.sportsbook.equals(bk, ignoreCase = true) }

                            if (outcome != null) {
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(
                                        Modifier
                                            .padding(4.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("${outcome.line}", style = MaterialTheme.typography.bodySmall)
                                        Text(formatAmerican(outcome.odds.toDoubleOrNull() ?: 0.0), style = MaterialTheme.typography.bodyLarge)
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    Divider()
                }
            }
        }
    }
}

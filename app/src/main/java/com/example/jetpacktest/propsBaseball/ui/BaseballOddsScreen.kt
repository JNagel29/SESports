package com.example.jetpacktest.propsBaseball.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpacktest.propsBaseball.model.BaseballProp
import com.example.jetpacktest.propsBaseball.viewmodel.BaseballOddsViewModel

@Composable
fun BaseballOddsScreen(
    eventId: String,
    homeTeam: String? = null,
    awayTeam: String? = null,
    viewModel: BaseballOddsViewModel = viewModel()
) {
    val props by viewModel.props
    val error by viewModel.error

    var selectedMarket by remember { mutableStateOf("player_strikeouts") }

    LaunchedEffect(eventId, selectedMarket) {
        viewModel.fetchBaseballProps(eventId, selectedMarket)
    }

    val filteredProps = if (!homeTeam.isNullOrEmpty() && !awayTeam.isNullOrEmpty()) {
        props.filter { prop ->
            prop.team.contains(homeTeam, ignoreCase = true) ||
                    prop.team.contains(awayTeam, ignoreCase = true)
        }
    } else {
        props
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Select Stat Type", style = MaterialTheme.typography.titleMedium)

        DropdownMenuBox(
            options = listOf("player_strikeouts", "player_hits", "player_total_bases"),
            selectedOption = selectedMarket,
            onOptionSelected = { selectedMarket = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            error.isNotEmpty() -> {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            }
            filteredProps.isNotEmpty() -> {
                LazyColumn {
                    items(filteredProps) { prop ->
                        BaseballPropItem(prop)
                    }
                }
            }
            else -> {
                Text("Loading props...")
            }
        }
    }
}

@Composable
fun DropdownMenuBox(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedOption.replace('_', ' ').replaceFirstChar { it.uppercase() })
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.replace('_', ' ').replaceFirstChar { it.uppercase() }) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun BaseballPropItem(prop: BaseballProp) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${prop.playerName} - ${prop.statType}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Line: ${prop.line}")
            Text("Team: ${prop.team} vs ${prop.opponent}")
        }
    }
}

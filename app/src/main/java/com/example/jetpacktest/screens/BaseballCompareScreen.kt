package com.example.jetpacktest.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jetpacktest.DatabaseHandler


@Composable
fun BaseballCompareScreen(
    navigateToCompareResults: (String, Boolean, String, Boolean) -> Unit
) {
    val databaseHandler = DatabaseHandler()
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedPlayers by remember { mutableStateOf<List<Pair<String, Boolean>>>(emptyList()) }
    var showAlertDialog by remember { mutableStateOf(false) }

    var searchPitchers by remember { mutableStateOf(true) } // <-- NEW: true = search pitchers, false = search batters

    Column(modifier = Modifier.padding(10.dp)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = { searchPitchers = !searchPitchers }) {
                Text(
                    text = if (searchPitchers) "Switch to Batters" else "Switch to Pitchers"
                )
            }
        }

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchText,
            singleLine = true,
            onValueChange = {
                searchText = it
                if (searchPitchers) {
                    databaseHandler.executePitcherSearch(searchText) { newResults ->
                        searchResults = newResults.take(5)
                    }
                } else {
                    databaseHandler.executeBatterSearch(searchText) { newResults ->
                        searchResults = newResults.take(5)
                    }
                }
            },
            label = { Text("Search Baseball Players...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(searchResults) { playerName ->
                Text(
                    text = playerName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            if (selectedPlayers.size < 2) {
                                val isPitcher = searchPitchers // <-- Directly based on toggle
                                selectedPlayers = selectedPlayers + (playerName to isPitcher)
                            } else {
                                showAlertDialog = true
                            }
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        if (selectedPlayers.isNotEmpty()) {
            Card(
                modifier = Modifier.padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Selected Players",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn {
                        items(selectedPlayers) { (player, _) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(player)
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    selectedPlayers = selectedPlayers.filter { it.first != player }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Remove Player",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                    Button(
                        onClick = {
                            if (selectedPlayers.size == 2) {
                                val (player1, isPitcher1) = selectedPlayers[0]
                                val (player2, isPitcher2) = selectedPlayers[1]
                                navigateToCompareResults(player1, isPitcher1, player2, isPitcher2)
                            } else {
                                showAlertDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("Compare", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = { Text("Warning") },
            text = { Text("You must select exactly 2 players.") },
            confirmButton = {
                Button(onClick = { showAlertDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

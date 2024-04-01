package com.example.jetpacktest.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.DatabaseHandler


@Composable
fun CompareScreen(
    navigateToCompareResults: (String, String) -> Unit
) {

    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var searchResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedPlayers by remember { mutableStateOf<List<String>>(emptyList()) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showSelectionDialog by remember { mutableStateOf(false) }
    val databaseHandler = DatabaseHandler()

    Column(modifier = Modifier.padding(10.dp)) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            value = searchText,
            singleLine = true,
            onValueChange = {
                searchText = it
                databaseHandler.executePlayerSearchResults(searchText) { newResults ->
                    searchResults = newResults.take(5)
                }
            },
            label = {
                Text("Compare Players ...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            colors = TextFieldDefaults.colors(
                cursorColor = Color.Blue,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(searchResults) { itemName ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            if (selectedPlayers.size < 2) {
                                selectedPlayers = selectedPlayers + itemName
                            } else {
                                // Display warning or exception for selecting more than two players
                                // For now, just print a warning
                                showAlertDialog = true
                                println("Cannot select more than two players")
                            }
                        }
                ) {
                    Text(text = itemName)
                }
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        if (selectedPlayers.isNotEmpty()) {
            Card(
                modifier = Modifier.padding(8.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp // Adds a 'shadow' effect
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Selected Players",
                            style = TextStyle(
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            ),
                        )
                    }
                    LazyColumn {
                        items(selectedPlayers) { playerName ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = playerName)
                                Spacer(modifier = Modifier.weight(1f))
                                // Red X button to remove player
                                Box(modifier = Modifier
                                    .clickable {
                                        selectedPlayers = selectedPlayers
                                            .filter { it != playerName }
                                    }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Remove Player",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (selectedPlayers.size < 2) {
                                    showSelectionDialog = true
                                } else {
                                    navigateToCompareResults(selectedPlayers[0], selectedPlayers[1])
                                }
                            },
                        ) {
                            Text(text = "Compare", textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = { Text("Warning") },
            text = { Text("You can only select 2 players") },
            confirmButton = {
                Button(
                    onClick = { showAlertDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
    if (showSelectionDialog) {
        AlertDialog(
            onDismissRequest = { showSelectionDialog = false },
            title = { Text("Warning") },
            text = { Text("Please select at least 2 players to compare") },
            confirmButton = {
                Button(
                    onClick = { showSelectionDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
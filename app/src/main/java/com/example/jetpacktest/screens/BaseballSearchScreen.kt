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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.DatabaseHandler

// Predefined lists for testing fallback
private val TestBatters = listOf("Test Batter 1")
private val TestPitchers = listOf("Test Pitcher 1")

val MLBTeams = listOf(
    "Yankees", "Red Sox", "Blue Jays", "Rays", "Orioles",
    "White Sox", "Guardians", "Twins", "Royals", "Tigers",
    "Astros", "Mariners", "Angels", "Rangers", "Athletics",
    "Braves", "Phillies", "Mets", "Marlins", "Nationals",
    "Brewers", "Cardinals", "Cubs", "Pirates", "Reds",
    "Dodgers", "Padres", "Giants", "Diamondbacks", "Rockies"
)

@Composable
fun BaseballSearchScreen(
    navigateToBaseballPlayerProfile: (String, Boolean) -> Unit,
    navigateToTeamProfile: (String) -> Unit
) {
    val databaseHandler = DatabaseHandler()

    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var searchType by remember { mutableStateOf("Pitchers") }

    val searchTypes = listOf("Batters", "Pitchers", "Teams")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = searchText,
            onValueChange = {
                searchText = it
                when (searchType) {
                    "Pitchers" -> databaseHandler.executePitcherSearch(searchText) { results ->
                        searchResults = TestPitchers + results
                    }
                    "Batters" -> databaseHandler.executeBatterSearch(searchText) { results ->
                        searchResults = TestBatters + results
                    }
                    else -> {} // Teams search not modified
                }
            },
            label = { Text("Search players or teams") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Search by:", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            DropdownMenuButton(
                selectedOption = searchType,
                onOptionSelected = { selected ->
                    searchType = selected
                    searchText = ""
                    searchResults = emptyList()
                },
                options = searchTypes
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(searchResults) { result ->
                SearchResultRow(
                    name = result,
                    onClick = {
                        when (searchType) {
                            "Teams" -> navigateToTeamProfile(result)
                            else -> {
                                val isPitcher = searchType == "Pitchers"
                                navigateToBaseballPlayerProfile(result, isPitcher)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SearchResultRow(name: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(
            text = name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Divider()
    }
}

@Composable
fun DropdownMenuButton(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selectedOption)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

package com.example.jetpacktest.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//Material 3
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
//Composable/UI
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.FavoritesHandler
import com.example.jetpacktest.ui.components.CircularLoadingIcon

@Composable
fun SearchScreen(
     searchText: State<String>,
     isSearching: State<Boolean>,
     playerResults: State<List<String>>,
     selectedSearchType: State<String>,
     teamResults: State<List<String>>,
     onSearchTextChange: (String) -> Unit,
     onSearchTypeChange: (String) -> Unit,
     clearResults: () -> Unit,
     navigateToPlayerProfile: (String) -> Unit,
     navigateToTeamProfile: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchTextValue = searchText.value
    val isSearchingValue = isSearching.value
    val selectedSearchTypeValue = selectedSearchType.value
    val playerResultsValue = playerResults.value
    val teamResultsValue = teamResults.value

    val favoritesHandler = FavoritesHandler(LocalContext.current)
    val favoritePlayers by remember { mutableStateOf(favoritesHandler.getFavoritePlayers())}
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            //First, we call function to display our radio buttons
            RadioButtonsDisplay(
                selectedSearchType = selectedSearchTypeValue,
                onSearchTypeSelected = { searchType ->
                    onSearchTypeChange(searchType)
                },
                onSearchTextChange = { newText ->
                    onSearchTextChange(newText)
                },
                onClearSearchResults = clearResults
            )
            //Add slight vertical space between radio buttons and search bar
            Spacer(modifier = Modifier.height(6.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                value = searchTextValue,
                singleLine = true,
                onValueChange = { onSearchTextChange(it) },
                label = {
                    Text(if (selectedSearchType.value == "Player") "Search Players ..."
                    else "Search Teams...")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon")
                },
                colors = TextFieldDefaults.colors(
                    cursorColor = Color.Blue,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
            //Add vertical space between search and list
            Spacer(modifier = Modifier.height(16.dp))
            if (isSearchingValue && searchTextValue != "") {
                CircularLoadingIcon()
            }
            else { // Display results after search
                if (selectedSearchTypeValue == "Player" && searchTextValue.isEmpty()) {
                    Text(
                        text = "Favorites",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                SearchResultsDisplay(
                    searchResults = if (selectedSearchTypeValue == "Player"
                                    && searchTextValue.isEmpty()) favoritePlayers.toList()
                                    else if (selectedSearchTypeValue == "Player") playerResultsValue
                                    else teamResultsValue,
                    hideKeyboard = { keyboardController!!.hide() },
                    navigateToProfile = { nameArg ->
                        if (selectedSearchTypeValue == "Player") navigateToPlayerProfile(nameArg)
                        else if (selectedSearchTypeValue == "Team") navigateToTeamProfile(nameArg)
                    }
                )
            }
        }
    }
}

//This function sets up our radio buttons to pick b/w player/team
@Composable
fun RadioButtonsDisplay(selectedSearchType: String, onSearchTypeSelected: (String) -> Unit,
                        onSearchTextChange: (String) -> Unit, onClearSearchResults: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center, // Puts button in center
        verticalAlignment = Alignment.CenterVertically // Aligns text with buttons
    ){
        //Nested function to handle click events for both radio buttons and text
        fun handleClick(searchType: String) {
            if (selectedSearchType != searchType) {
                onSearchTypeSelected(searchType)
                onSearchTextChange("")
                onClearSearchResults()
            }
        }
        //Player/Team Buttons
        RadioButton(
            selected = selectedSearchType == "Player",
            onClick = { handleClick("Player")},
            colors = RadioButtonDefaults.colors(Color.Blue)
        )
        Text(
            text = "Player",
            modifier = Modifier.clickable { handleClick("Player") }
        )
        RadioButton(
            selected = selectedSearchType == "Team",
            onClick = {handleClick("Team")
            },
            colors = RadioButtonDefaults.colors(Color.Blue)
        )
        Text(
            text = "Team",
            modifier = Modifier.clickable { handleClick("Team") }
        )
    }
}


//This function actually displays our results
@Composable
fun SearchResultsDisplay(
    searchResults: List<String>,
    hideKeyboard: () -> Unit,
    navigateToProfile: (String) -> (Unit)
    ) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(searchResults) { itemName ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        hideKeyboard()
                        //Navigate using lambda that routes profile screen
                        navigateToProfile(itemName) // Pass in item (aka player/team name)
                    }
            ) {
                Text(text = itemName)
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
}
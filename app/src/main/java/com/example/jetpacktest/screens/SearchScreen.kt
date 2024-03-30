package com.example.jetpacktest.screens

import SearchViewModel
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
//Material 3
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.models.NbaTeam
import com.example.jetpacktest.navigation.Screens

@Composable
fun SearchScreen(searchText: State<String>,
                 isSearching: State<Boolean>,
                 playerResults: State<List<String>>,
                 onSearchTextChange: (String) -> Unit,
                 clearPlayerResults: () -> Unit,
                 navigateToPlayerProfile: (String) -> Unit,
                 navigateToTeamProfile: (String) -> Unit) {
    var selectedSearchType by remember {
        mutableStateOf("Player") // Default to player on launch
    }
    //val searchText by searchViewModel.searchText.collectAsState()
    //val isSearching by searchViewModel.isSearching.collectAsState()
    //val playerResults by searchViewModel.playerResults.collectAsState()
    // Retrieve the values from the State objects
    val searchTextValue = searchText.value
    val isSearchingValue = isSearching.value
    val playerResultsValue = playerResults.value


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            //First, we call function to display our radio buttons
            RadioButtonsDisplay(
                selectedSearchType = selectedSearchType,
                onSearchTypeSelected = { searchType ->
                    selectedSearchType = searchType
                },
                onSearchTextChange = { newText ->
                    onSearchTextChange(newText)
                },
                onClearSearchResults = clearPlayerResults
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
                    Text(if (selectedSearchType == "Player") "Search Players ..."
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
            if (isSearchingValue && searchTextValue != "") { //Loading circle while searching
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            else { // Display results after search
                SearchResultsDisplay(searchResults = playerResultsValue) { nameArg ->
                    if (selectedSearchType == "Player") navigateToPlayerProfile(nameArg)
                    else if (selectedSearchType == "Team") navigateToTeamProfile(nameArg)
                }
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
        //First our player button
        RadioButton(
            //If var equals Player, then we know it's selected and so it gets a check on it
            selected = selectedSearchType == "Player",
            //Set var to Player on click
            //We use the lambda that sets the searchTypeSelected from the caller function, to Team
            onClick = { handleClick("Player")},
            colors = RadioButtonDefaults.colors(Color.Blue)
        )
        Text(
            text = "Player",
            modifier = Modifier.clickable { handleClick("Player") }
        )
        //Then, team button
        RadioButton(
            //If var equals Team, then we know it's selected and so it gets a check on it
            selected = selectedSearchType == "Team",
            //Set var to Team on click
            //We use the lambda that sets the searchTypeSelected from the caller function, to Team
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
fun SearchResultsDisplay(searchResults: List<String>, navigateToProfile: (String) -> (Unit)) {
    //Now, our 'recyclerview' to display search results
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        //items is function that loops through our list of results and displays it
        items(searchResults) { itemName ->
            Box( // We use Box wrapping text to make it so entire row is clickable, not just text
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        //Navigate using lambda that routes profile screen
                        navigateToProfile(itemName) // Pass in item (aka player/team name)
                    }
            ) {
                Text(text = itemName)
            }
        }
    }
}

//This function handles searching for teams
fun handleTeamSearch(searchedTeamName: String, onSearchResult: (List<String>) -> Unit) {
    val teamNames = mutableListOf<String>() // Instantiate an empty list for matching team names
    //Loop through each team name that we have in NbaTeam model object
    for (nbaTeamName in NbaTeam.names) {
        //If name contains search text as a substring (ignoring case), then add it to list
        if (nbaTeamName.contains(other = searchedTeamName, ignoreCase = true)) {
            teamNames.add(nbaTeamName)
        }
    }
    onSearchResult(teamNames) // Call back to SearchScreen() w/ lambda the list so it can display
}

@Preview
@Composable
fun SearchScreenPreview() {
    //Dummy navController/view model for previewing
    val navController = rememberNavController()
    val searchViewModel = viewModel<SearchViewModel>()
    //SearchScreen()
}
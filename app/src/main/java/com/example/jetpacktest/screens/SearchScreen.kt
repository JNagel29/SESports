package com.example.jetpacktest.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
//For Volley
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.jetpacktest.models.NbaTeam
import com.example.jetpacktest.navigation.Screens
import org.json.JSONException
import org.json.JSONObject

@Composable
fun SearchScreen(navController: NavController) {
    //For holding search text and for clearing focus
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    //For holding state of results, needed b/c can't call composable/change UI inside handleSearch
    var searchResults by remember { mutableStateOf<List<String>>(emptyList()) }
    //Context that we pass into handleSearch for Volley
    val context = LocalContext.current
    //Variable to hold radio button choice (search by player, or team)
    var selectedSearchType by remember { // mutableStateOf makes it so whenever it changes, recompose
        mutableStateOf("Player") // Default to player since that'll be most common
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            //First, we call function to display our radio buttons
            //We also pass a lambda that we'll need to change the searchType onclick in the fun
            RadioButtonsDisplay(selectedSearchType) { searchType ->
                selectedSearchType  = searchType
            }
            //Add slight vertical space between radio buttons and search bar
            Spacer(modifier = Modifier.height(6.dp))
            //Now, our search bar (in the form of a read-only text-field)
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                value = searchText, // Store text in there
                singleLine = true, // Ensure user can only input one line
                onValueChange = {
                    searchText = it // Update text our text field on change
                    //If we're searching teams, we can also update for each value change
                    //We won't do this for players since parsing API that much could be slow
                    if (selectedSearchType == "Team") handleTeamSearch(searchText) { newResults ->
                        searchResults = newResults
                    }
                },
                label = { // Label to tell user what to do, changes based off search type
                    Text(if (selectedSearchType == "Player") "Search Players ..."
                    else "Search Teams...")
                },
                leadingIcon = { // Make search icon to left of our text
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon")
                },
                keyboardActions = KeyboardActions(
                    //When user submits their search, invokes this
                    onDone = {
                        //Go to function to handle it
                        //We use lambda that takes in string newResults, and saves in searchResults
                        if (selectedSearchType == "Player") {
                            handlePlayerSearch(searchText, context) { newResults ->
                                searchResults = newResults
                            }
                        }
                        //If user wants to search teams, we must call appropriate function for it
                        else {
                            handleTeamSearch(searchText)  { newResults ->
                                searchResults = newResults
                            }
                        }
                        //Also, clear the focus after submitting to dismiss keyboard
                        focusManager.clearFocus()
                    }
                ),
                // Final param is the colors for the field
                colors = TextFieldDefaults.colors(
                    cursorColor = Color.Blue, // Set cursor color
                    focusedIndicatorColor = Color.Transparent, // Set focused indicator color
                    unfocusedIndicatorColor = Color.Transparent, // Set unfocused indicator color
                )
            )
            //Add vertical space between search and list
            Spacer(modifier = Modifier.height(16.dp))
            //Finally, we can display Search results via lazy column
            SearchResultsDisplay(searchResults = searchResults) {nameArg ->
                //We have to pass in the lambda that navigates to the new screen using navController
                if (selectedSearchType == "Player")
                    navController.navigate("${Screens.ProfileScreen.route}/$nameArg")
                else if (selectedSearchType == "Team")
                    //TODO: Possibly switch this to another screen if we ever need to separate
                    navController.navigate("${Screens.TeamProfileScreen.route}/$nameArg")
            }
        }
    }
}

//This function sets up our radio buttons to pick b/w player/team
@Composable
fun RadioButtonsDisplay(selectedSearchType: String,
                        onSearchTypeSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center, // Puts button in center
        verticalAlignment = Alignment.CenterVertically // Aligns text with buttons
    ){ // Enclose two radio buttons (with their text) on same row
        //First our player button
        RadioButton(
            //If var equals Player, then we know it's selected and so it gets a check on it
            selected = selectedSearchType == "Player",
            //Set var to Player on click
            //We use the lambda that sets the searchTypeSelected from the caller function, to Team
            //TODO: add searchText = "" here to reset search bar, wasn't working
            onClick = { onSearchTypeSelected("Player")
                       },
            colors = RadioButtonDefaults.colors(Color.Blue)
        )
        Text(text = "Player")
        //Then, team button
        RadioButton(
            //If var equals Team, then we know it's selected and so it gets a check on it
            selected = selectedSearchType == "Team",
            //Set var to Team on click
            //We use the lambda that sets the searchTypeSelected from the caller function, to Team
            onClick = { onSearchTypeSelected("Team")},
            colors = RadioButtonDefaults.colors(Color.Blue)
        )
        Text(text = "Team")
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

//This function matches playerName to a list of player names to show list
fun handlePlayerSearch(searchedPlayerName: String, context: Context, onSearchResult: (List<String>) -> Unit) {
    val basePlayerUrl = "https://nba-stats-db.herokuapp.com/api/playerdata/name/"
    val playerURL = basePlayerUrl + searchedPlayerName // Concatenates the full URL by appending name
    val playerNames = mutableListOf<String>() // Instantiate an empty mutable list

    //Now, we make the request using Volley
    val request = JsonObjectRequest(
        Request.Method.GET,
        playerURL,
        null,
        { response ->
            try {
                val arrPlayers =
                    response.getJSONArray("results") // Grabs each player from each szn as array
                var playerInstance: JSONObject
                var playerName: String
                //Loops through array and grabs each JSONObject and checks its name
                for (i in 0 until arrPlayers.length()) {
                    playerInstance = arrPlayers.getJSONObject(i)
                    playerName = playerInstance.getString("player_name")
                    var playerFound = false
                    //Loops through our current list of names and see if we already have that name
                    for (j in playerNames.indices) {
                        if (playerNames[j] == playerName) playerFound = true
                    }
                    //If there was a duplicate player, then don't add (since there's a field for each
                    //year someone has played, which would lead to duplicate search results w/out this
                    if (!playerFound) playerNames.add(playerName)
                }
                // Update the list of search results, list is almost global and remembers so compos. can use it
                onSearchResult(playerNames)
            } catch (e: JSONException) {
                e.printStackTrace() // Print error in logcat
            }
        }) { error ->
        error.printStackTrace() // Print error in logcat
    }
    Volley.newRequestQueue(context.applicationContext).add(request)
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
    //Dummy navController for previewing
    val navController = rememberNavController()
    SearchScreen(navController = navController)
}
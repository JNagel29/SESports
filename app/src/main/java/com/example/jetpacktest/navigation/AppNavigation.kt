package com.example.jetpacktest.navigation

import SearchViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetpacktest.screens.CompareResultsScreen
import com.example.jetpacktest.screens.CompareScreen
import com.example.jetpacktest.screens.HomeScreen
import com.example.jetpacktest.screens.ProfileScreen
import com.example.jetpacktest.screens.SearchScreen
import com.example.jetpacktest.screens.GamesScreen
import com.example.jetpacktest.screens.StandingsScreen
import com.example.jetpacktest.screens.TeamProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val searchViewModel = viewModel<SearchViewModel>()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                //Loop through each nav item and set it up as a navigation bar item
                listOfNavItems.forEach { navItem ->
                    NavigationBarItem(
                        //We know it's selected if current route matches item route
                        selected = currentDestination?.hierarchy?.any
                        { it.route == navItem.route } == true,
                        onClick = {
                                  navController.navigate(navItem.route) {
                                      popUpTo(navController.graph.findStartDestination().id) {
                                          saveState = true
                                      }
                                      //Prevents creating multiple instances of same screen
                                      launchSingleTop = true
                                      //Restores state when re-selecting a previously selected item
                                      restoreState = true
                                  }
                        },
                        icon = {
                            Icon(imageVector = navItem.icon, // Use icon of current navItem
                                contentDescription = navItem.label)
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) {paddingValues ->
        NavHost(navController = navController,
            startDestination = Screens.HomeScreen.name,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize() // Fixes weird transition effect
        ) {
            //When passing navigation control, we pass lambdas instead of navController itself
            composable(route=Screens.HomeScreen.name) {
                HomeScreen(
                    //Pass in a lambda that'll let us go to a stat leader's profile on click
                    navigateToPlayerProfile = { playerName ->
                        navController.navigate("${Screens.ProfileScreen.route}/$playerName")
                    }
                )
            }
            composable(route=Screens.SearchScreen.name) {
                SearchScreen(
                    //States and lambdas from view model (we never want to pass entire VM)
                    searchText = searchViewModel.searchText.collectAsState(),
                    isSearching = searchViewModel.isSearching.collectAsState(),
                    selectedSearchType = searchViewModel.selectedSearchType.collectAsState(),
                    playerResults = searchViewModel.playerResults.collectAsState(),
                    teamResults = searchViewModel.teamResults.collectAsState(),
                    onSearchTextChange ={ newText ->
                        searchViewModel.onSearchTextChange(newText)
                    },
                    onSearchTypeChange = { newType ->
                       searchViewModel.onSearchTypeChanged(newType)
                    },
                    clearResults = { searchViewModel.clearResults() },
                    //Two more lambdas that will let us access team/player profiles on click
                    navigateToPlayerProfile = { playerName ->
                        navController.navigate("${Screens.ProfileScreen.route}/$playerName")
                    },
                    navigateToTeamProfile = { teamName ->
                        navController.navigate("${Screens.TeamProfileScreen.route}/$teamName")
                    }
                )
            }
            composable(route = Screens.CompareScreen.name) {
                CompareScreen(
                    navigateToCompareResults = { playerName1, playerName2 ->
                        navController.navigate("${Screens.CompareResultsScreen.route}/$playerName1/$playerName2")
                    }
                )
            }
            composable(route = Screens.StandingsScreen.name) {
                    StandingsScreen()
            }
            composable(route=Screens.GamesScreen.name) {
                GamesScreen(
                    //Pass in lambda that lets us go to team profile from logo click
                    navigateToTeamProfile = { teamName ->
                        navController.navigate("${Screens.TeamProfileScreen.route}/$teamName")
                    }
                )
            }
            composable(route = "${Screens.CompareResultsScreen.route}/{playerName1}/{playerName2}")
            { backStackEntry ->
                val playerName1 = backStackEntry.arguments?.getString("playerName1") ?: ""
                val playerName2 = backStackEntry.arguments?.getString("playerName2") ?: ""
                CompareResultsScreen(playerName1, playerName2) {
                    navController.popBackStack()
                }
            }
            //Profile screens for player/team (we pass in player/team name as arg in route), not on nav bar
            composable(route = "${Screens.ProfileScreen.route}/{playerName}")
            { backStackEntry ->
                val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
                ProfileScreen(playerName) {
                    //Used for back button
                    navController.navigateUp()
                }
            }
            composable(route = "${Screens.TeamProfileScreen.route}/{teamName}")
            { backStackEntry ->
                val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
                TeamProfileScreen(teamName = teamName,
                    //Used for back button
                    navigateBack = { navController.popBackStack()},
                    //When user clicks on a current player of a team, we'll use this to switch over
                    navigateToPlayerProfile = { playerName ->
                        navController.navigate("${Screens.ProfileScreen.route}/$playerName") }
                )
            }
        }

    }
}
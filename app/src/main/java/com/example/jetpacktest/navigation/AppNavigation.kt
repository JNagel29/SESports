package com.example.jetpacktest.navigation

import android.content.Intent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.example.jetpacktest.viewmodels.HomeViewModel
import com.example.jetpacktest.viewmodels.SearchViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.jetpacktest.screens.SplashScreen
import com.example.jetpacktest.screens.StandingBracketPager
import com.example.jetpacktest.screens.TeamProfileScreen
import com.example.jetpacktest.props.ui2.EventsActivity              // ← our new feature’s entry point
import com.example.jetpacktest.viewmodels.StandingsViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(randomStat: String) {
    val navController = rememberNavController()
    val ctx = LocalContext.current                         // ← need this to launch the Activity
    val searchViewModel = viewModel<SearchViewModel>()
    val homeViewModel = viewModel<HomeViewModel>()
    val standingsViewModel = viewModel<StandingsViewModel>()
    val getPreviousScreenName: () -> (String?) = {
        navController.previousBackStackEntry?.destination?.route
    }
    val outerNavBackStackEntry by navController.currentBackStackEntryAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope() // Required for snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
        bottomBar = {
            if (outerNavBackStackEntry?.destination?.route != Screens.SplashScreen.name) {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    // ─── existing items ──────────────────────────────────
                    listOfNavItems.forEach { navItem ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
                            onClick = {
                                navController.navigate(navItem.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState     = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector    = navItem.icon,
                                    contentDescription = navItem.label
                                )
                            },
                            label = { Text(navItem.label) }
                        )
                    }

                    // ─── NEW: Props feature button ───────────────────────
                    NavigationBarItem(
                        selected = false,
                        onClick  = {
                            ctx.startActivity(
                                Intent(ctx, EventsActivity::class.java)
                                    .putExtra("randomStat", randomStat)
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = "Props"
                            )
                        },
                        label = { Text("Props") }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.SplashScreen.name,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // ─── all your existing composable routes ────────────
            composable(route = Screens.SplashScreen.name) {
                SplashScreen {
                    navController.navigate(Screens.HomeScreen.name)
                }
            }
            composable(route = Screens.HomeScreen.name) {
                HomeScreen(
                    randomStat           = randomStat,
                    chosenStatFlow       = homeViewModel.chosenStatFlow,
                    chosenYearFlow       = homeViewModel.chosenYearFlow,
                    statLeadersListFlow  = homeViewModel.statLeadersListFlow,
                    fetchStatLeaders     = { homeViewModel.fetchStatLeaders() },
                    updateChosenStat     = { homeViewModel.updateChosenStat(newStat = it) },
                    updateChosenYear     = { homeViewModel.updateChosenYear(newYear = it) },
                    navigateToPlayerProfile = { playerName ->
                        navController.navigate("${Screens.ProfileScreen.route}/$playerName") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(route = Screens.SearchScreen.name) {
                SearchScreen(
                    searchText            = searchViewModel.searchText.collectAsState(),
                    isSearching           = searchViewModel.isSearching.collectAsState(),
                    selectedSearchType    = searchViewModel.selectedSearchType.collectAsState(),
                    playerResults         = searchViewModel.playerResults.collectAsState(),
                    teamResults           = searchViewModel.teamResults.collectAsState(),
                    onSearchTextChange    = { searchViewModel.onSearchTextChange(it) },
                    onSearchTypeChange    = { searchViewModel.onSearchTypeChanged(it) },
                    clearResults          = { searchViewModel.clearResults() },
                    navigateToPlayerProfile = { playerName ->
                        navController.navigate("${Screens.ProfileScreen.route}/$playerName") {
                            launchSingleTop = true
                        }
                    },
                    navigateToTeamProfile = { teamName ->
                        navController.navigate("${Screens.TeamProfileScreen.route}/$teamName") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(route = Screens.CompareScreen.name) {
                CompareScreen(
                    navigateToCompareResults = { p1, p2 ->
                        navController.navigate("${Screens.CompareResultsScreen.route}/$p1/$p2") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(route = Screens.StandingsBracketPager.name) {
                StandingBracketPager(
                    westernFlow           = standingsViewModel.westernFlow,
                    easternFlow           = standingsViewModel.easternFlow,
                    navigateToTeamProfile = { team ->
                        navController.navigate("${Screens.TeamProfileScreen.route}/$team") {
                            launchSingleTop = true
                        }
                    },
                    updateStandingsByYear = { year ->
                        standingsViewModel.updateWesternStandings(year)
                        standingsViewModel.updateEasternStandings(year)
                    },
                    yearOptions           = standingsViewModel.yearOptions
                )
            }
            composable(route = Screens.GamesScreen.name) {
                GamesScreen(
                    navigateToTeamProfile = { team ->
                        navController.navigate("${Screens.TeamProfileScreen.route}/$team") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(
                route = "${Screens.CompareResultsScreen.route}/{playerName1}/{playerName2}",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300, easing = FastOutLinearInEasing)
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(300, easing = FastOutLinearInEasing)
                    )
                }
            ) { backStackEntry ->
                val playerName1 = backStackEntry.arguments?.getString("playerName1") ?: ""
                val playerName2 = backStackEntry.arguments?.getString("playerName2") ?: ""
                CompareResultsScreen(playerName1, playerName2) {
                    navController.popBackStack()
                }
            }
            composable(
                route = "${Screens.ProfileScreen.route}/{playerName}",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300, easing = FastOutLinearInEasing)
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(300, easing = FastOutLinearInEasing)
                    )
                }
            ) { backStackEntry ->
                val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
                ProfileScreen(
                    playerName            = playerName,
                    navigateBack          = { navController.navigateUp() },
                    getPreviousScreenName = getPreviousScreenName,
                    showSnackBar          = { msg ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(msg)
                        }
                    }
                )
            }
            composable(
                route = "${Screens.TeamProfileScreen.route}/{teamName}",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300, easing = FastOutLinearInEasing)
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(300, easing = FastOutLinearInEasing)
                    )
                }
            ) { backStackEntry ->
                val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
                TeamProfileScreen(
                    teamName                = teamName,
                    navigateBack            = { navController.popBackStack() },
                    navigateToPlayerProfile = { player ->
                        navController.navigate("${Screens.ProfileScreen.route}/$player") {
                            launchSingleTop = true
                        }
                    },
                    getPreviousScreenName   = getPreviousScreenName
                )
            }
        }
    }
}

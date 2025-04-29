package com.example.jetpacktest.navigation

import BaseballHomeScreen
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.jetpacktest.screens.*
import com.example.jetpacktest.viewmodel.BaseballHomeViewModel
import com.example.jetpacktest.viewmodels.BaseballSearchViewModel
import com.example.jetpacktest.viewmodels.BaseballStandingsViewModel
import com.example.jetpacktest.viewmodels.HomeViewModel
import com.example.jetpacktest.viewmodels.SearchViewModel
import com.example.jetpacktest.viewmodels.StandingsViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(randomStat: String) {
    val navController = rememberNavController()
    val searchViewModel = viewModel<SearchViewModel>()
    val homeViewModel = viewModel<HomeViewModel>()
    val standingsViewModel = viewModel<StandingsViewModel>()
    val getPreviousScreenName: () -> (String?) = {
        navController.previousBackStackEntry?.destination?.route
    }
    val baseballStandingsViewModel = viewModel<BaseballStandingsViewModel>()
    val outerNavBackStackEntry by navController.currentBackStackEntryAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var isBasketballMenuVisible by remember { mutableStateOf(true) }
    var isBaseballMenuVisible by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            Row {
                FloatingActionButton(onClick = {
                    isBasketballMenuVisible = !isBasketballMenuVisible
                    if (isBasketballMenuVisible) isBaseballMenuVisible = false
                }) {
                    Icon(
                        imageVector = if (isBasketballMenuVisible) Icons.Default.KeyboardArrowDown
                        else Icons.Default.SportsBasketball,
                        contentDescription = "Toggle Basketball Menu"
                    )
                }

                Spacer(modifier = Modifier.padding(start = 12.dp))

                FloatingActionButton(onClick = {
                    isBaseballMenuVisible = !isBaseballMenuVisible
                    if (isBaseballMenuVisible) isBasketballMenuVisible = false
                }) {
                    Icon(
                        imageVector = if (isBaseballMenuVisible) Icons.Default.KeyboardArrowDown
                        else Icons.Default.SportsBaseball,
                        contentDescription = "Toggle Baseball Menu"
                    )
                }
            }
        },
        bottomBar = {
            val route = outerNavBackStackEntry?.destination?.route
            if (route != Screens.SplashScreen.name) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                when {
                    isBasketballMenuVisible -> {
                        NavigationBar {
                            listOfNavItems.forEach { navItem ->
                                NavigationBarItem(
                                    selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
                                    onClick = {
                                        navController.navigate(navItem.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                                    },
                                    label = {
                                        Text(text = navItem.label)
                                    }
                                )
                            }
                        }
                    }

                    isBaseballMenuVisible -> {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentDestination?.route == Screens.BaseballScreen.name,
                                onClick = {
                                    navController.navigate(Screens.BaseballScreen.name) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home") }
                            )

                            NavigationBarItem(
                                selected = currentDestination?.route == Screens.BaseballStandingsScreen.name,
                                onClick = {
                                    navController.navigate(Screens.BaseballStandingsScreen.name) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Analytics, contentDescription = "Standings") },
                                label = { Text("Standings") }
                            )
                            NavigationBarItem(
                                selected = currentDestination?.route == Screens.BaseballSearchScreen.name,
                                onClick = {
                                    navController.navigate(Screens.BaseballSearchScreen.name) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                label = { Text("Search") }
                            )
                            NavigationBarItem(
                                selected = currentDestination?.route == Screens.BaseballCompareScreen.name,
                                onClick = {
                                    navController.navigate(Screens.BaseballCompareScreen.name) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.CompareArrows, contentDescription = "Compare") },
                                label = { Text("Compare") }
                            )
                        }
                    }
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
            composable(route = Screens.SplashScreen.name) {
                SplashScreen {
                    navController.navigate(Screens.HomeScreen.name)
                }
            }

            composable(route = Screens.HomeScreen.name) {
                HomeScreen(
                    randomStat = randomStat,
                    chosenStatFlow = homeViewModel.chosenStatFlow,
                    chosenYearFlow = homeViewModel.chosenYearFlow,
                    statLeadersListFlow = homeViewModel.statLeadersListFlow,
                    fetchStatLeaders = { homeViewModel.fetchStatLeaders() },
                    updateChosenStat = { homeViewModel.updateChosenStat(it) },
                    updateChosenYear = { homeViewModel.updateChosenYear(it) },
                    navigateToPlayerProfile = { playerName ->
                        navController.navigate("${Screens.ProfileScreen.route}/$playerName") {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(route = Screens.BaseballScreen.name) {
                val baseballViewModel = viewModel<BaseballHomeViewModel>()
                BaseballHomeScreen(
                    randomStat = "Shohei Ohtani hit 2 HR and pitched 10Ks in the same game.",
                    chosenStatFlow = baseballViewModel.chosenStatFlow,
                    chosenYearFlow = baseballViewModel.chosenYearFlow,
                    statLeadersListFlow = baseballViewModel.statLeadersListFlow,
                    fetchStatLeaders = { isPitcher, stat, year ->
                        baseballViewModel.fetchBaseballLeaders(isPitcher, stat, year)
                    },
                    updateChosenStat = { baseballViewModel.updateChosenStat(it) },
                    updateChosenYear = { baseballViewModel.updateChosenYear(it) },
                    navigateToPlayerProfile = { navController.navigate("baseball_player_profile/$it") }
                )
            }





            composable(route = Screens.BaseballStandingsScreen.name) {
                BaseballStandingsScreen(
                    americanFlow = baseballStandingsViewModel.americanFlow,
                    nationalFlow = baseballStandingsViewModel.nationalFlow,
                    updateStandingsByYear = {
                        baseballStandingsViewModel.updateAmericanStandings(it)
                        baseballStandingsViewModel.updateNationalStandings(it)
                    },
                    yearOptions = baseballStandingsViewModel.yearOptions
                )
            }

            composable("${Screens.BaseballProfileScreen.route}/{playerName}/{isPitcher}") { backStackEntry ->
                val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
                val isPitcher = backStackEntry.arguments?.getString("isPitcher")?.toBoolean() ?: true
                BaseballProfileScreen(
                    playerName = playerName,
                    isPitcher = isPitcher,
                    navigateBack = { navController.popBackStack() },
                    showSnackBar = { message -> /* show snackbar */ }
                )
            }







            composable(route = Screens.SearchScreen.name) {
                SearchScreen(
                    searchText = searchViewModel.searchText.collectAsState(),
                    isSearching = searchViewModel.isSearching.collectAsState(),
                    selectedSearchType = searchViewModel.selectedSearchType.collectAsState(),
                    playerResults = searchViewModel.playerResults.collectAsState(),
                    teamResults = searchViewModel.teamResults.collectAsState(),
                    onSearchTextChange = { searchViewModel.onSearchTextChange(it) },
                    onSearchTypeChange = { searchViewModel.onSearchTypeChanged(it) },
                    clearResults = { searchViewModel.clearResults() },
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
                CompareScreen { player1, player2 ->
                    navController.navigate("${Screens.CompareResultsScreen.route}/$player1/$player2") {
                        launchSingleTop = true
                    }
                }
            }

            val navigateToTeamProfile: (String) -> Unit = { teamName ->
                navController.navigate("${Screens.TeamProfileScreen.route}/$teamName") {
                    launchSingleTop = true
                }
            }

            composable(route = Screens.StandingsBracketPager.name) {
                StandingBracketPager(
                    westernFlow = standingsViewModel.westernFlow,
                    easternFlow = standingsViewModel.easternFlow,
                    navigateToTeamProfile = navigateToTeamProfile,
                    updateStandingsByYear = {
                        standingsViewModel.updateWesternStandings(it)
                        standingsViewModel.updateEasternStandings(it)
                    },
                    yearOptions = standingsViewModel.yearOptions
                )
            }
            composable(route = Screens.BaseballSearchScreen.name) {
                BaseballSearchScreen(
                    navigateToBaseballPlayerProfile = { playerName, isPitcher ->
                        navController.navigate("${Screens.BaseballProfileScreen.route}/$playerName/$isPitcher") {
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




            composable(route = Screens.GamesScreen.name) {
                GamesScreen(navigateToTeamProfile = navigateToTeamProfile)
            }

            composable(
                route = "${Screens.CompareResultsScreen.route}/{playerName1}/{playerName2}",
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300, easing = FastOutLinearInEasing))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300, easing = FastOutLinearInEasing))
                }
            ) { backStackEntry ->
                val p1 = backStackEntry.arguments?.getString("playerName1") ?: ""
                val p2 = backStackEntry.arguments?.getString("playerName2") ?: ""
                CompareResultsScreen(p1, p2) {
                    navController.popBackStack()
                }
            }

            composable("${Screens.ProfileScreen.route}/{playerName}") { backStackEntry ->
                val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
                ProfileScreen(
                    playerName = playerName,
                    navigateBack = { navController.navigateUp() },
                    getPreviousScreenName = getPreviousScreenName,
                    showSnackBar = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(it)
                        }
                    }
                )
            }
            composable(route = Screens.BaseballCompareScreen.name) {
                BaseballCompareScreen { player1, isPitcher1, player2, isPitcher2 ->
                    navController.navigate(
                        "${Screens.BaseballCompareResultScreen.route}/$player1/$isPitcher1/$player2/$isPitcher2"
                    ) {
                        launchSingleTop = true
                    }
                }
            }


            composable(
                route = "${Screens.BaseballCompareResultScreen.route}/{playerName1}/{isPitcher1}/{playerName2}/{isPitcher2}",
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300, easing = FastOutLinearInEasing))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300, easing = FastOutLinearInEasing))
                }
            ) { backStackEntry ->
                val playerName1 = backStackEntry.arguments?.getString("playerName1") ?: ""
                val isPitcher1 = backStackEntry.arguments?.getString("isPitcher1")?.toBooleanStrictOrNull() ?: false

                val playerName2 = backStackEntry.arguments?.getString("playerName2") ?: ""
                val isPitcher2 = backStackEntry.arguments?.getString("isPitcher2")?.toBooleanStrictOrNull() ?: false

                val player1 = BaseballComparePlayer(playerName = playerName1, isPitcher = isPitcher1)
                val player2 = BaseballComparePlayer(playerName = playerName2, isPitcher = isPitcher2)

                BaseballCompareResultScreen(
                    player1 = player1,
                    player2 = player2,
                    navigateBack = { navController.popBackStack() }
                )
            }



            composable("${Screens.TeamProfileScreen.route}/{teamName}") { backStackEntry ->
                val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
                TeamProfileScreen(
                    teamName = teamName,
                    navigateBack = { navController.popBackStack() },
                    navigateToPlayerProfile = { playerName ->
                        navController.navigate("${Screens.ProfileScreen.route}/$playerName") {
                            launchSingleTop = true
                        }
                    },
                    getPreviousScreenName = getPreviousScreenName
                )
            }
        }
    }
}

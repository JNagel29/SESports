package com.example.jetpacktest.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetpacktest.screens.GameScreen
import com.example.jetpacktest.screens.HomeScreen
import com.example.jetpacktest.screens.ProfileScreen
import com.example.jetpacktest.screens.SearchScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                listOfNavItems.forEach { navItem ->
                    NavigationBarItem(
                        //We know it's selected if current route matches item route
                        selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
                        onClick = {
                                  navController.navigate(navItem.route) {
                                      popUpTo(navController.graph.findStartDestination().id) {
                                          saveState = true
                                      }
                                      //Prevents creating multiple instances of same screen when spamming
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
        ) {
            composable(route=Screens.HomeScreen.name) {
                HomeScreen()
            }
            //We pass navController into this one since it'll need it to switch to a profile
            composable(route=Screens.SearchScreen.name) {
                SearchScreen(navController = navController)
            }
            composable(route=Screens.GameScreen.name) {
                GameScreen()
            }
            composable(route = "${Screens.ProfileScreen.route}/{playerName}") { backStackEntry ->
                val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
                ProfileScreen(playerName) {
                    navController.popBackStack()
                }
            }

        }

    }
}
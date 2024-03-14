package com.example.jetpacktest.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val listOfNavItems = listOf(
    NavItem(
        label = "Home",
        icon = Icons.Default.Home,
        route = Screens.HomeScreen.name // Returns enum name as string
    ),
    NavItem(
        label = "Search",
        icon = Icons.Default.Search,
        route = Screens.SearchScreen.name // Returns enum name as string
    ),
    NavItem(
        label ="Compare",
        icon = Icons.AutoMirrored.Filled.CompareArrows,
        route = Screens.CompareScreen.name // Returns enum name as string
    ),
    NavItem(
        label = "Standings",
        icon = Icons.Default.Analytics,
        route = Screens.StandingsScreen.name // Returns enum name as string
    )
)
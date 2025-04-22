package com.example.jetpacktest.navigation

enum class Screens(val route: String) {
    HomeScreen("home"),
    SplashScreen("splash"),
    SearchScreen("search"),
    CompareScreen("compare"),
    CompareResultsScreen("compareresults"),
    ProfileScreen("profile"),
    TeamProfileScreen("teamprofile"),
    StandingsBracketPager("standingbracketpager"), // Redirects us to Standings/BracketScreen
    StandingsScreen("standings"),
    BracketsScreen("brackets"),
    GamesScreen("games"),
}
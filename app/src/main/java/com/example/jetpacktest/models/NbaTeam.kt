package com.example.jetpacktest.models

import com.example.jetpacktest.R

//Used in TeamProfile and Standings
//For fetching standings, fetching player names of team we wont use an object since its only name field)
data class Team(
    val city: String,
    val name: String,
    val conference: Conference,
    val wins: Int,
    val losses: Int,
    val percentage: Double
)
enum class Conference {
    WESTERN,
    EASTERN
}
object NbaTeam { // We use object so it's a singleton that we don't have to instantiate like a class
    //Team Names, used in SearchScreen.kt when searching for teams
    val names = listOf(
        "Atlanta Hawks",
        "Boston Celtics",
        "Brooklyn Nets",
        "Charlotte Hornets",
        "Chicago Bulls",
        "Cleveland Cavaliers",
        "Dallas Mavericks",
        "Denver Nuggets",
        "Detroit Pistons",
        "Golden State Warriors",
        "Houston Rockets",
        "Indiana Pacers",
        "Los Angeles Clippers",
        "Los Angeles Lakers",
        "Memphis Grizzlies",
        "Miami Heat",
        "Milwaukee Bucks",
        "Minnesota Timberwolves",
        "New Orleans Pelicans",
        "New York Knicks",
        "Oklahoma City Thunder",
        "Orlando Magic",
        "Philadelphia Sixers",
        "Phoenix Suns",
        "Portland Trail Blazers",
        "Sacramento Kings",
        "San Antonio Spurs",
        "Toronto Raptors",
        "Utah Jazz",
        "Washington Wizards"
    )
    //Maps above team names to its abbreviation, used in TeamProfileScreen for getting playerNames
    val namesToAbbreviations = mapOf(
        "Atlanta Hawks" to "ATL",
        "Boston Celtics" to "BOS",
        "Brooklyn Nets" to "BKN",
        "Charlotte Hornets" to "CHA",
        "Chicago Bulls" to "CHI",
        "Cleveland Cavaliers" to "CLE",
        "Dallas Mavericks" to "DAL",
        "Denver Nuggets" to "DEN",
        "Detroit Pistons" to "DET",
        "Golden State Warriors" to "GSW",
        "Houston Rockets" to "HOU",
        "Indiana Pacers" to "IND",
        "Los Angeles Clippers" to "LAC",
        "Los Angeles Lakers" to "LAL",
        "Memphis Grizzlies" to "MEM",
        "Miami Heat" to "MIA",
        "Milwaukee Bucks" to "MIL",
        "Minnesota Timberwolves" to "MIN",
        "New Orleans Pelicans" to "NOP",
        "New York Knicks" to "NYK",
        "Oklahoma City Thunder" to "OKC",
        "Orlando Magic" to "ORL",
        "Philadelphia Sixers" to "PHI",
        "Phoenix Suns" to "PHX",
        "Portland Trail Blazers" to "POR",
        "Sacramento Kings" to "SAC",
        "San Antonio Spurs" to "SAS",
        "Toronto Raptors" to "TOR",
        "Utah Jazz" to "UTA",
        "Washington Wizards" to "WAS")

        // Define a mapping of team abbreviations to drawable resource IDs for the logos
        //TODO: Implement this, replace unknown_player with actual drawable logo
        val logos: Map<String, Int> = mapOf(
            "Atlanta Hawks" to R.drawable.unknown_player,
            "Boston Celtics" to R.drawable.unknown_player,
        )

}
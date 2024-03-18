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
        "New Orleans Pelicans" to "NO",
        "New York Knicks" to "NY",
        "Oklahoma City Thunder" to "OKC",
        "Orlando Magic" to "ORL",
        "Philadelphia Sixers" to "PHI",
        "Phoenix Suns" to "PHO",
        "Portland Trail Blazers" to "POR",
        "Sacramento Kings" to "SAC",
        "San Antonio Spurs" to "SA",
        "Toronto Raptors" to "TOR",
        "Utah Jazz" to "UTA",
        "Washington Wizards" to "WAS"
    )

    // Define a mapping of team abbreviations to drawable resource IDs for the logos
    val logos: Map<String, Int> = mapOf(
        "Atlanta Hawks" to R.drawable.hawks,
        "Boston Celtics" to R.drawable.celtics,
        "Brooklyn Nets" to R.drawable.nets,
        "Charlotte Hornets" to R.drawable.hornets,
        "Chicago Bulls" to R.drawable.bulls,
        "Cleveland Cavaliers" to R.drawable.cavs,
        "Dallas Mavericks" to R.drawable.mavs,
        "Denver Nuggets" to R.drawable.nuggets,
        "Detroit Pistons" to R.drawable.pistons,
        "Golden State Warriors" to R.drawable.warriors,
        "Houston Rockets" to R.drawable.rockets,
        "Indiana Pacers" to R.drawable.pacers,
        "Los Angeles Clippers" to R.drawable.clippers,
        "Los Angeles Lakers" to R.drawable.lakers,
        "Memphis Grizzlies" to R.drawable.grizzlies,
        "Miami Heat" to R.drawable.heat,
        "Milwaukee Bucks" to R.drawable.bucks,
        "Minnesota Timberwolves" to R.drawable.wolves,
        "New Orleans Pelicans" to R.drawable.pelicans,
        "New York Knicks" to R.drawable.knicks,
        "Oklahoma City Thunder" to R.drawable.thunder,
        "Orlando Magic" to R.drawable.magic,
        "Philadelphia Sixers" to R.drawable.sixers,
        "Phoenix Suns" to R.drawable.suns,
        "Portland Trail Blazers" to R.drawable.blazers,
        "Sacramento Kings" to R.drawable.kings,
        "San Antonio Spurs" to R.drawable.spurs,
        "Toronto Raptors" to R.drawable.raptors,
        "Utah Jazz" to R.drawable.jazz,
        "Washington Wizards" to R.drawable.wizards
    )

}
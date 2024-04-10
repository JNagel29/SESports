package com.example.jetpacktest.models

import com.example.jetpacktest.R

//Used in TeamProfile and Standings
//For fetching standings, fetching player names of team we wont use an object since its only name field)
data class TeamStanding(
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
        "Golden State Warriors" to "GS",
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

    //Define a mapping of team abbreviations to drawable resource IDs for the logos
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

    //Map names to xml drawables, used in GamesScreen
    val xmlLogos: Map<String, Int> = mapOf(
        "Hawks" to R.drawable.xml_hawks,
        "Celtics" to R.drawable.xml_celtics,
        "Nets" to R.drawable.xml_nets,
        "Hornets" to R.drawable.xml_hornets,
        "Bulls" to R.drawable.xml_bulls,
        "Cavaliers" to R.drawable.xml_cavaliers,
        "Mavericks" to R.drawable.xml_mavericks,
        "Nuggets" to R.drawable.xml_nuggets,
        "Pistons" to R.drawable.xml_pistons,
        "Warriors" to R.drawable.xml_warriors,
        "Rockets" to R.drawable.xml_rockets,
        "Pacers" to R.drawable.xml_pacers,
        "Clippers" to R.drawable.xml_clippers,
        "Lakers" to R.drawable.xml_lakers,
        "Grizzlies" to R.drawable.xml_grizzlies,
        "Heat" to R.drawable.xml_heat,
        "Bucks" to R.drawable.xml_bucks,
        "Timberwolves" to R.drawable.xml_timberwolves,
        "Pelicans" to R.drawable.xml_pelicans,
        "Knicks" to R.drawable.xml_knicks,
        "Thunder" to R.drawable.xml_thunder,
        "Magic" to R.drawable.xml_magic,
        "76ers" to R.drawable.xml_sixers,
        "Suns" to R.drawable.xml_suns,
        "Trail Blazers" to R.drawable.xml_trailblazers,
        "Kings" to R.drawable.xml_kings,
        "Spurs" to R.drawable.xml_spurs,
        "Raptors" to R.drawable.xml_raptors,
        "Jazz" to R.drawable.xml_jazz,
        "Wizards" to R.drawable.xml_wizards
    )

    val shortenedNamesToFullNames: Map<String, String> = mapOf(
        "Hawks" to "Atlanta Hawks",
        "Lakers" to "Los Angeles Lakers",
        "Celtics" to "Boston Celtics",
        "Warriors" to "Golden State Warriors",
        "Bulls" to "Chicago Bulls",
        "Heat" to "Miami Heat",
        "Knicks" to "New York Knicks",
        "Raptors" to "Toronto Raptors",
        "Spurs" to "San Antonio Spurs",
        "Clippers" to "Los Angeles Clippers",
        "Mavericks" to "Dallas Mavericks",
        "Nuggets" to "Denver Nuggets",
        "Rockets" to "Houston Rockets",
        "Trail Blazers" to "Portland Trail Blazers",
        "Bucks" to "Milwaukee Bucks",
        "Pacers" to "Indiana Pacers",
        "Grizzlies" to "Memphis Grizzlies",
        "Thunder" to "Oklahoma City Thunder",
        "Suns" to "Phoenix Suns",
        "Timberwolves" to "Minnesota Timberwolves",
        "Pelicans" to "New Orleans Pelicans",
        "Kings" to "Sacramento Kings",
        "Magic" to "Orlando Magic",
        "Wizards" to "Washington Wizards",
        "Cavaliers" to "Cleveland Cavaliers",
        "Hornets" to "Charlotte Hornets",
        "Pistons" to "Detroit Pistons",
        "Jazz" to "Utah Jazz",
        "76ers" to "Philadelphia Sixers",
        "Nets" to "Brooklyn Nets"
    )

    val teamColorsMap = mapOf(
        "LAL" to R.color.lal,
        "PHO" to R.color.phx,
        "DAL" to R.color.dal,
        "TOT" to R.color.sas, // San Antonio Spurs (formerly known as the Dallas Chaparrals)
        "MIA" to R.color.mia,
        "CLE" to R.color.cle,
        "WSB" to R.color.was,
        "MIL" to R.color.mil,
        "CHI" to R.color.chi,
        "GSW" to R.color.gsw,
        "MIN" to R.color.min,
        "IND" to R.color.ind,
        "WAS" to R.color.was,
        "BOS" to R.color.bos,
        "HOU" to R.color.hou,
        "SAC" to R.color.sac,
        "DEN" to R.color.den,
        "ORL" to R.color.orl,
        "NOH" to R.color.nop, // New Orleans Pelicans (formerly New Orleans Hornets)
        "TOR" to R.color.tor,
        "CHO" to R.color.cha, // Charlotte Hornets (formerly New Orleans Hornets)
        "PHI" to R.color.phi,
        "ATL" to R.color.atl,
        "POR" to R.color.por,
        "DET" to R.color.det,
        "OKC" to R.color.okc,
        "UTA" to R.color.uta,
        "VAN" to R.color.mem, // Memphis Grizzlies (formerly Vancouver Grizzlies)
        "SEA" to R.color.okc, // Oklahoma City Thunder (formerly Seattle SuperSonics)
        "NJN" to R.color.bkn, // Brooklyn Nets (formerly New Jersey Nets)
        "NOK" to R.color.nop, // New Orleans Pelicans (formerly New Orleans Hornets)
        "LAC" to R.color.lac,
        "CHA" to R.color.cha,
        "MEM" to R.color.mem,
        "NYK" to R.color.nyk,
        "NOP" to R.color.nop,
        "BRK" to R.color.bkn,
        "SAS" to R.color.sas,
        "CHH" to R.color.cha // Charlotte Hornets (formerly Charlotte Bobcats)
    )

}
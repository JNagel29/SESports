package com.example.jetpacktest.models

data class GameResponse(
    val data: List<Game>
)

data class Game(
    var status: String, //TODO fix var
    val period: Int,
    var time: String?, //TODO fix var
    val home_team_score: Int,
    val visitor_team_score: Int,
    val home_team: Team,
    val visitor_team: Team
)

data class Team(
    val id: Int,
    val conference: String,
    val division: String,
    val city: String,
    val name: String,
    val full_name: String,
    val abbreviation: String,
    var logo: Int
    //val logo: Int = NbaTeam.xmlLogos[name] ?: R.drawable.fallback
)
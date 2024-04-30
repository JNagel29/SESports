package com.example.jetpacktest.models

import com.google.gson.annotations.SerializedName

data class GameResponse(
    val data: List<Game>
)

data class Game(
    var status: String,
    val period: Int,
    var time: String?,
    @SerializedName("home_team_score")
    val homeTeamScore: Int,
    @SerializedName("visitor_team_score")
    val visitorTeamScore: Int,
    @SerializedName("home_team")
    val homeTeam: Team,
    @SerializedName("visitor_team")
    val visitorTeam: Team
)

data class Team(
    val id: Int,
    val conference: String,
    val division: String,
    val city: String,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val abbreviation: String,
    var logo: Int //TODO fix var
)
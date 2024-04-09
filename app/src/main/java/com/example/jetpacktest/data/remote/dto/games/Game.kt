package com.example.jetpacktest.data.remote.dto.games


import com.google.gson.annotations.SerializedName

data class Game(
    val id: Int,
    val date: String,
    val season: Int,
    var status: String, // TODO: Varring for now
    val period: Int,
    var time: String?, // TODO: Varring for now
    val postseason: Boolean,
    @SerializedName("home_team_score")
    val homeTeamScore: Int,
    @SerializedName("visitor_team_score")
    val visitorTeamScore: Int,
    @SerializedName("home_team")
    val homeTeam: Team,
    @SerializedName("visitor_team")
    val visitorTeam: Team
)
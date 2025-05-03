package com.example.jetpacktest.props.network.models

import com.example.jetpacktest.props.network.Bookmaker
import com.example.jetpacktest.props.network.Market
import com.example.jetpacktest.props.network.Outcome
import com.google.gson.annotations.SerializedName

/**
 * Top‑level game with its list of bookmakers & markets.
 */
data class GameOdds(
    val id: String,
    @SerializedName("sport_key") val sportKey: String,
    @SerializedName("sport_title") val sportTitle: String,
    @SerializedName("commence_time") val commenceTime: String,
    @SerializedName("home_team") val homeTeam: String,
    @SerializedName("away_team") val awayTeam: String,
    val bookmakers: List<Bookmaker>
)

data class Bookmaker(
    val key: String,
    val title: String,
    @SerializedName("last_update") val lastUpdate: String,
    val markets: List<Market>
)

data class Market(
    val key: String,
    val outcomes: List<Outcome>
)

data class Outcome(
    val name: String,
    val price: Int,
    val point: Double? = null
)

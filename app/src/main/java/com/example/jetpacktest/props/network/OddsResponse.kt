package com.example.jetpacktest.props.network

import com.example.jetpacktest.props.network.models.Bookmaker
import com.example.jetpacktest.props.network.models.Outcome

data class OddsResponse(
    val id: String,
    val sport_key: String,
    val commence_time: String,
    val home_team: String,
    val away_team: String,
    val bookmakers: List<Bookmaker>
)

data class Bookmaker(
    val key: String,
    val title: String,
    val last_update: String,
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

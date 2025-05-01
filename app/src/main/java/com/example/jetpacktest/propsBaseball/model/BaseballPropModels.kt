package com.example.jetpacktest.propsBaseball.model

data class Bookmaker(
    val title: String,
    val markets: List<Market>
)

data class Market(
    val key: String,
    val outcomes: List<Outcome>
)

data class Outcome(
    val name: String,
    val price: Double,
    val point: Double? = null
)

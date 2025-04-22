// app/src/main/java/com/example/sportify2/network/models/PlayerOdds.kt
package com.example.jetpacktest.props.network.models

import com.google.gson.annotations.SerializedName

data class PlayerOdds(
    @SerializedName("market_label")
    val key: String,

    @SerializedName("player")
    val player: Player,

    @SerializedName("selections")
    val selections: List<Selection>
) {
    val playerName: String get() = "${player.name}"
    val title: String get() = key
    val point: Double? get() = selections.firstOrNull()?.books?.firstOrNull()?.line?.line
    val price: Double get()   = selections.firstOrNull()?.books?.firstOrNull()?.line?.cost ?: 0.0
}

data class Player(
    @SerializedName("name") val name: String,
    @SerializedName("position") val position: String,
    @SerializedName("team") val team: String
)

data class Selection(
    @SerializedName("label") val label: String,
    @SerializedName("books") val books: List<Book>
)

data class Book(
    @SerializedName("bookie") val bookie: String,
    @SerializedName("line") val line: Line
)

data class Line(
    @SerializedName("line") val line: Double,
    @SerializedName("cost") val cost: Double
)

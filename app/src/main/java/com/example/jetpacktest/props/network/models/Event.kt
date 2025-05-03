// app/src/main/java/com/example/sportify2/network/models/Event.kt
package com.example.jetpacktest.props.network.models

import com.google.gson.annotations.SerializedName

/**
 * Matches the JSON:
 * [
 *   {
 *     "Date":"2025-04-18",
 *     "id":26393,
 *     "sport":"NBA",
 *     "teams":{
 *       "home":{ "abbreviation":"ATL","city":"Atlanta","name":"Hawks" },
 *       "away":{ "abbreviation":"MIA","city":"Miami","name":"Heat" }
 *     }
 *   },
 *   ...
 * ]
 */
data class Event(
    @SerializedName("id")
    val id: Int,

    @SerializedName("Date")
    private val date: String,

    @SerializedName("teams")
    private val teams: Teams
) {
    /** e.g. “Atlanta Hawks” */
    val homeTeam: String
        get() = "${teams.home.city} ${teams.home.name}"

    /** e.g. “Miami Heat” */
    val awayTeam: String
        get() = "${teams.away.city} ${teams.away.name}"

    /** e.g. “2025-04-18” */
    val commenceTime: String
        get() = date
}

data class Teams(
    @SerializedName("home") val home: Team,
    @SerializedName("away") val away: Team
)

data class Team(
    @SerializedName("abbreviation") val abbreviation: String,
    @SerializedName("city")         val city: String,
    @SerializedName("name")         val name: String
)

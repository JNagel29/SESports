package com.example.jetpacktest.props.network

import com.example.jetpacktest.props.network.models.Event
import com.example.jetpacktest.props.network.models.PlayerOdds
import retrofit2.http.GET
import retrofit2.http.Query

interface PropsApiService {

    /** Fetch all events for a given ISO date (yyyy‑MM‑dd) */
    @GET("get-events-for-date")
    suspend fun getEventsForDate(
        @Query("date") dateIso: String
    ): List<Event>

    /** Fetch all player‑prop odds for one event */
    @GET("get-player-odds-for-event")
    suspend fun getPlayerOddsForEvent(
        @Query("eventId") eventId: String,
        @Query("marketId") marketIds: String?   = null,
        @Query("bookieId") bookieIds: String?   = null,
        @Query("decimal")   decimal: Boolean    = false,
        @Query("best")      best: Boolean       = true
    ): List<PlayerOdds>
}

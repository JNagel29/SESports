package com.example.jetpacktest.props

import com.example.jetpacktest.props.network.models.Bookie
import com.example.jetpacktest.props.network.models.Event
import com.example.jetpacktest.props.network.models.Market
import com.example.jetpacktest.props.network.models.PlayerOdds
import retrofit2.http.GET
import retrofit2.http.Query

interface RapidApiService {
    /** 1) Fetch today’s events (you can optionally add a date param if supported) */
    @GET("get-events-for-date")
    suspend fun getEventsForDate(
        @Query("date") dateIso: String? = null
    ): List<Event>

    /** 2) Fetch all player‑props odds for a given event */
    @GET("get-player-odds-for-event")
    suspend fun getPlayerOddsForEvent(
        @Query("eventId")   eventId: String,
        @Query("bookieId") bookieIds: String? = null,  // comma‑delimited bookmaker IDs
        @Query("marketId") marketIds: String? = null,  // comma‑delimited market IDs
        @Query("decimal")  decimal:   Boolean = true,   // decimal vs american odds
        @Query("best")     best:      Boolean = true    // only best offers
    ): List<PlayerOdds>

    /** 3) Lookup available markets (e.g. “points”, “rebounds”, etc.) */
    @GET("get-markets")
    suspend fun getMarkets(
        @Query("name") nameFilter: String? = null
    ): List<Market>

    /** 4) Lookup available bookies (e.g. “draftkings”, “fanduel”, etc.) */
    @GET("get-bookies")
    suspend fun getBookies(
        @Query("name") nameFilter: String? = null
    ): List<Bookie>
}

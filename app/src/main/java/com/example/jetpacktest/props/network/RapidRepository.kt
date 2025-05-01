package com.example.jetpacktest.props.network

import com.example.jetpacktest.props.network.models.Event
import com.example.jetpacktest.props.network.models.PlayerOdds

class RapidRepository {
    private val api = RapidApiRetrofitClient.apiService

    /** 1) All events on a given date (pass null for today) */
    suspend fun getEventsForDate(dateIso: String? = null): List<Event> =
        api.getEventsForDate(dateIso)

    /** 2) All player‐prop odds for one event */
    suspend fun getPlayerOddsForEvent(
        eventId: String,
        bookieIds: String? = null,
        marketIds: String? = null,
        decimal: Boolean = true,
        best: Boolean = true
    ): List<PlayerOdds> =
        api.getPlayerOddsForEvent(
            eventId   = eventId,
            bookieIds = bookieIds,
            marketIds = marketIds,
            decimal   = decimal,
            best      = best
        )
}

package com.example.jetpacktest.props.network

import com.example.jetpacktest.props.network.models.GameOdds

class OddsRepository {
    private val api = RetrofitClient.apiService
    private val defaultSport = "basketball_nba"

    /** All upcoming games with odds */
    suspend fun getUpcomingOdds(apiKey: String): List<GameOdds> =
        api.getOdds(sportKey = defaultSport, apiKey = apiKey)

    /** Single game by its event ID */
    suspend fun getOddsForEvent(apiKey: String, eventId: String): GameOdds? {
        val list = api.getOddsForEvent(
            sportKey = defaultSport,
            apiKey = apiKey,
            eventIds = eventId
        )
        return list.firstOrNull()
    }
}

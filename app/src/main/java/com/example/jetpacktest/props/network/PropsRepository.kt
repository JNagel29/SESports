package com.example.jetpacktest.props.network

import com.example.jetpacktest.props.network.models.Event
import com.example.jetpacktest.props.network.models.PlayerOdds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PropsRepository {
    private val api = RetrofitClient.propsService

    /**
     * Fetch events for today (offsetDays = 0),
     * tomorrow (offsetDays = 1), yesterday (offsetDays = -1), etc.
     */
    suspend fun todayEvents(offsetDays: Int = 0): List<Event> = withContext(Dispatchers.IO) {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, offsetDays)
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dateIso = sdf.format(cal.time)
        api.getEventsForDate(dateIso)
    }

    /** Fetch all player‑prop odds for a given event */
    suspend fun playerOdds(eventId: String): List<PlayerOdds> =
        api.getPlayerOddsForEvent(eventId)
}

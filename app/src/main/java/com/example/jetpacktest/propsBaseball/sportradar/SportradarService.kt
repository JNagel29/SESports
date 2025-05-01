package com.example.jetpacktest.propsBaseball.sportradar.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

object SportradarService {

    private const val API_KEY = "4OxHodzl76B2xWXcpeMn9RTywZ9fAD08gLc9weXo"
    private const val BASE_URL = "https://api.sportradar.com/oddscomparison-liveodds/trial/v2/en/"

    private val client = OkHttpClient()

    fun getTodayMlbGames(): Response? {
        val url = BASE_URL +
                "sports/sr%3Asport%3A2/schedules/date/sport_events_schedules.json"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("accept", "application/json")
            .addHeader("api-key", API_KEY)
            .build()

        return client.newCall(request).execute()
    }
}

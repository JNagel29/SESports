// FILE 1: SportradarApiService.kt
package com.example.jetpacktest.propsBaseball.sportradar

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SportradarApiService {
    // Correct schedule endpoint for player props
    @GET("oddscomparison-playerprops/trial/v2/en/sports/sr:sport:2/schedules/date/{date}/sport_events_schedules.json")
    fun getBaseballSchedule(
        @Path("date") date: String,
        @Query("api_key") apiKey: String = "4OxHodzl76B2xWXcpeMn9RTywZ9fAD08gLc9weXo"
    ): Call<ResponseBody>

    // Player props for a specific event
    @GET("oddscomparison-playerprops/trial/v2/en/sport_events/{event_id}/players_props.json")
    fun getPlayerProps(
        @Path("event_id") eventId: String,
        @Query("api_key") apiKey: String = "4OxHodzl76B2xWXcpeMn9RTywZ9fAD08gLc9weXo"
    ): Call<ResponseBody>
}

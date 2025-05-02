package com.example.jetpacktest.propsBaseball.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BaseballOddsApiService {

    // Step 1: Get all MLB events
    @GET("v4/sports/baseball_mlb/events")
    fun getMlbEvents(
        @Query("apiKey") apiKey: String = "4312c01e209032a3e8c465c2910cd5b9"
    ): Call<ResponseBody>

    // Step 2: Get props for a specific event
    @GET("v4/sports/baseball_mlb/events/{event_id}/odds")
    fun getPropsForEvent(
        @Path("event_id") eventId: String,
        @Query("regions") region: String = "us",
        @Query("markets") market: String = "player_props",
        @Query("apiKey") apiKey: String = "4312c01e209032a3e8c465c2910cd5b9"
    ): Call<ResponseBody>
}

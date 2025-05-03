package com.example.jetpacktest.props.network

import com.example.jetpacktest.props.network.models.GameOdds
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OddsApiService {

    /** Fetch all upcoming games for a sport */
    @GET("/v4/sports/{sport}/odds")
    suspend fun getOdds(
        @Path("sport") sportKey: String,
        @Query("apiKey") apiKey: String,
        @Query("regions") regions: String = "us",
        @Query("markets") markets: String = "h2h,spreads",
        @Query("oddsFormat") oddsFormat: String = "american",
        @Query("dateFormat") dateFormat: String = "iso"
    ): List<GameOdds>

    /** Fetch only specific event(s) by their ID(s) */
    @GET("/v4/sports/{sport}/odds")
    suspend fun getOddsForEvent(
        @Path("sport") sportKey: String,
        @Query("apiKey") apiKey: String,
        @Query("eventIds") eventIds: String,
        @Query("regions") regions: String = "us",
        @Query("markets") markets: String = "h2h,spreads",
        @Query("oddsFormat") oddsFormat: String = "american",
        @Query("dateFormat") dateFormat: String = "iso"
    ): List<GameOdds>
}

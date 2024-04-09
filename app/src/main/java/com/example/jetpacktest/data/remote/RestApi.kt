package com.example.jetpacktest.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface RestApi {
    /* TODO: Implement the stuff in rest_player package for this to work */
    @GET("player")
    suspend fun getPlayerByNameAndYear(@Query("name") name: String, @Query("year") year: Int)
            //: List<RestPlayerDto>
    @GET("search-players")
    suspend fun getPlayerSearchResults(@Query("name") name: String): List<String>
}
package com.example.jetpacktest.data.remote

import com.example.jetpacktest.common.Keys
import com.example.jetpacktest.data.remote.dto.games.GamesDto
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface BdlApi {
    @Headers("Authorization: ${Keys.BDLAPIKey}")
    @GET("games")
    suspend fun getGames(@Query("dates[]") date: String): GamesDto
}
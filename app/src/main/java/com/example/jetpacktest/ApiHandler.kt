package com.example.jetpacktest

import android.util.Log
import com.example.jetpacktest.models.PlayerPersonalInfo
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiHandler {
    private val basePlayerUrl = Keys.BDL_BASE_URL

    fun fetchPlayerData() {}

    suspend fun fetchPlayerInfo(playerName: String): PlayerPersonalInfo {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(basePlayerUrl)
            .build()
            .create(ApiInterface::class.java)
        val nameParts = playerName.split(" ".toRegex(), limit = 2)
        return try {
            retrofitBuilder.getPersonalInfo(
                firstName = nameParts[0],
                lastName = nameParts[1]
            )
        } catch (e: HttpException) {
            Log.d("ApiHandler", "HTTP Exception during API call: ${e.message}")
            PlayerPersonalInfo(emptyList())
        }

    }
}
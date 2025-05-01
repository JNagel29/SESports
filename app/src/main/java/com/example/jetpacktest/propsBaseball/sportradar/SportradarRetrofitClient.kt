package com.example.jetpacktest.propsBaseball.sportradar

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object SportradarRetrofitClient {

    private const val BASE_URL = "https://api.sportradar.com/oddscomparison-liveodds/trial/v2/en/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: SportradarApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SportradarApiService::class.java)
    }
}

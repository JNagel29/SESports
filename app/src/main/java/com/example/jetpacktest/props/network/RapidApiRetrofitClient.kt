package com.example.jetpacktest.props.network

import com.example.jetpacktest.props.RapidApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RapidApiRetrofitClient {
    private const val BASE_URL = "https://nba-player-props-odds.p.rapidapi.com/"
    private const val API_KEY = "ec12840b77mshaf973c799262dd3p14d7f5jsnbaf19c1f6b6e"
    private const val HOST    = "nba-player-props-odds.p.rapidapi.com"

    // Interceptor to add the required headers
    private val headerInterceptor = Interceptor { chain ->
        val newReq: Request = chain.request().newBuilder()
            .addHeader("x-rapidapi-key", API_KEY)
            .addHeader("x-rapidapi-host", HOST)
            .build()
        chain.proceed(newReq)
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .build()

    val apiService: RapidApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RapidApiService::class.java)
}

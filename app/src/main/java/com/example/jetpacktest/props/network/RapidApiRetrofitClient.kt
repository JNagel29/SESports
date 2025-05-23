package com.example.jetpacktest.props.network

import com.example.jetpacktest.props.RapidApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RapidApiRetrofitClient {
    private const val BASE_URL = "https://nba-player-props-odds.p.rapidapi.com/"
    private const val API_KEY = "1789e2d204msh12f314e2a1f19e9p150196jsnd8d55be5e1a1"
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

package com.example.jetpacktest.props.network

import com.example.jetpacktest.props.network.OddsApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // RapidAPI (props) constants
    private const val RAPIDAPI_KEY  = "44b0d91a57msh9b725c07bd8b849p1eda8cjsnc05beb64cc4b"
    private const val RAPIDAPI_HOST = "nba-player-props-odds.p.rapidapi.com"

    // TheOddsAPI constants
    private const val ODDSAPI_BASE = "https://api.the-odds-api.com/v4/"

    // Logging interceptor
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Headers for RapidAPI
    private val headerInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("x-rapidapi-key", RAPIDAPI_KEY)
            .addHeader("x-rapidapi-host", RAPIDAPI_HOST)
            .build()
        chain.proceed(request)
    }

    // OkHttp client for Props API
    private val rapidOkHttp = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(   30, TimeUnit.SECONDS)
        .writeTimeout(  30, TimeUnit.SECONDS)
        .addInterceptor(headerInterceptor)
        .addInterceptor(logging)
        .build()

    /** NBA player‑props API (RapidAPI) */
    val propsService: PropsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://$RAPIDAPI_HOST/")
            .client(rapidOkHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PropsApiService::class.java)
    }

    // OkHttp client for general Odds API
    private val oddsOkHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    /** Game odds API */
    val apiService: OddsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ODDSAPI_BASE)
            .client(oddsOkHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OddsApiService::class.java)
    }
}

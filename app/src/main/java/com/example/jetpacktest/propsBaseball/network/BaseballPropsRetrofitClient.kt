package com.example.jetpacktest.propsBaseball.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BaseballPropsRetrofitClient {
    val instance: BaseballOddsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.the-odds-api.com/")  // ends with a slash
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BaseballOddsApiService::class.java)
    }
}

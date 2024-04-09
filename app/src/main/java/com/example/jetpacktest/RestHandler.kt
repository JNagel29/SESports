package com.example.jetpacktest

import android.util.Log
import com.example.jetpacktest.models.Player
import com.example.jetpacktest.models.RestPlayer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestHandler {
    private val baseUrl = "http://192.168.0.171:8080/"

    suspend fun getPlayerSearchResults(searchResultName: String): List<String> {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(ApiInterface::class.java)
        val searchList = retrofitBuilder.getPlayerSearchResults(name = searchResultName)
        return searchList
    }



}
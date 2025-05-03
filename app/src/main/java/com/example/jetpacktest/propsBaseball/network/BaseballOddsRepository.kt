package com.example.jetpacktest.propsBaseball.network

import okhttp3.ResponseBody
import retrofit2.Call

class BaseballOddsRepository {
    private val apiService = BaseballOddsRetrofitClient.instance

    fun getMlbEvents(): Call<ResponseBody> {
        return apiService.getMlbEvents()
    }

    fun getPropsForEvent(eventId: String): Call<ResponseBody> {
        return apiService.getPropsForEvent(eventId)
    }
}

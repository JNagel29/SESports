package com.example.jetpacktest.propsBaseball.sportradar

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SportradarRepository {

    private val api = SportradarRetrofitClient.instance

    fun getBaseballSchedule(
        date: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        api.getBaseballSchedule(date).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    if (body != null) {
                        onSuccess(body)
                    } else {
                        onError("Empty schedule response")
                    }
                } else {
                    onError("Schedule error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onError("Schedule failure: ${t.message}")
            }
        })
    }

    fun getPlayerProps(
        eventId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        api.getPlayerProps(eventId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    if (body != null) {
                        onSuccess(body)
                    } else {
                        onError("Empty props response")
                    }
                } else {
                    onError("Props error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onError("Props failure: ${t.message}")
            }
        })
    }
}

package com.example.jetpacktest.propsBaseball.sportradar

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SportradarPlayerPropsRepository(private val apiService: SportradarApiService) {

    fun getPlayerProps(
        sportEventId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = apiService.getPlayerProps(sportEventId)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    if (!body.isNullOrEmpty()) {
                        onSuccess(body)
                    } else {
                        onError("Empty response body")
                    }
                } else {
                    onError("API Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onError("Network failure: ${t.message}")
            }
        })
    }
}

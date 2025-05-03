package com.example.jetpacktest.propsBaseball.network

import com.example.jetpacktest.propsBaseball.model.BaseballProp
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BaseballPropsRepository {

    fun getBaseballProps(
        eventId: String,
        market: String,
        onSuccess: (List<BaseballProp>) -> Unit,
        onError: (String) -> Unit
    ) {
        val call = BaseballRetrofitClient.instance.getPropsForEvent(eventId, market)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val bodyString = response.body()?.string()
                    if (bodyString != null) {
                        try {
                            val props = BaseballPropsParser.parsePropsFromJson(bodyString)
                            onSuccess(props)
                        } catch (e: Exception) {
                            onError("Parsing error: ${e.message}")
                        }
                    } else {
                        onError("Empty response")
                    }
                } else {
                    onError("API error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onError("Network error: ${t.message}")
            }
        })
    }
}

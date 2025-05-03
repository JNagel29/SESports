package com.example.jetpacktest.propsBaseball.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.propsBaseball.model.BaseballEvent
import com.example.jetpacktest.propsBaseball.network.BaseballOddsRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BaseballEventsViewModel : ViewModel() {

    private val repository = BaseballOddsRepository()

    private val _events = mutableStateOf<List<BaseballEvent>>(emptyList())
    val events: State<List<BaseballEvent>> = _events

    private val _error = mutableStateOf("")
    val error: State<String> = _error

    fun fetchEvents() {
        viewModelScope.launch {
            repository.getMlbEvents().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        response.body()?.string()?.let { body ->
                            val parsed = parseOddsApiEvents(body)
                            _events.value = listOf(
                                BaseballEvent(
                                    id = "static_world_series",
                                    homeTeam = "Los Angeles Dodgers",
                                    awayTeam = "New York Yankees",
                                    commenceTime = "2024-10-27T17:18:00Z"
                                )
                            ) + parsed
                            _error.value = ""
                        } ?: run {
                            _error.value = "Empty response"
                        }
                    } else {
                        _error.value = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    _error.value = "Failed to fetch events: ${t.message}"
                }
            })
        }
    }

    private fun parseOddsApiEvents(json: String): List<BaseballEvent> {
        val list = mutableListOf<BaseballEvent>()
        val root = JSONArray(json)

        for (i in 0 until root.length()) {
            val obj = root.getJSONObject(i)
            val id = obj.getString("id")
            val commenceTime = obj.getString("commence_time")

            val homeTeam = obj.optString("home_team", "Unknown")
            val awayTeam = obj.optString("away_team", "Unknown")

            list.add(BaseballEvent(id, homeTeam = homeTeam, awayTeam = awayTeam, commenceTime = commenceTime))
        }

        return list
    }
}

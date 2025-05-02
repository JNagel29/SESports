package com.example.jetpacktest.propsBaseball.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.propsBaseball.model.BaseballProp
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL

class BaseballOddsViewModel : ViewModel() {
    var props = mutableStateOf<List<BaseballProp>>(emptyList())
        private set

    var error = mutableStateOf("")
        private set

    fun fetchBaseballProps(eventId: String, market: String) {
        viewModelScope.launch {
            try {
                val url = "https://api.sportradar.us/oddscomparison-trial/v1/en/events/$eventId/playerprops.json?api_key=YOUR_API_KEY"
                val json = URL(url).readText()
                props.value = parseBaseballPropsFromJson(json, market)
                error.value = ""
            } catch (e: Exception) {
                error.value = "Failed to load props: ${e.message}"
            }
        }
    }

    private fun parseBaseballPropsFromJson(json: String, marketFilter: String): List<BaseballProp> {
        val result = mutableListOf<BaseballProp>()
        val root = JSONObject(json)
        val playerProps = root.getJSONArray("player_props")

        for (i in 0 until playerProps.length()) {
            val player = playerProps.getJSONObject(i)
            val playerName = player.getString("name")
            val competitorId = player.getString("competitor_id")
            val markets = player.getJSONArray("markets")

            for (j in 0 until markets.length()) {
                val market = markets.getJSONObject(j)
                val statType = market.getString("name")
                if (statType != marketFilter) continue

                val books = market.getJSONArray("books")
                for (k in 0 until books.length()) {
                    val book = books.getJSONObject(k)
                    val outcomes = book.getJSONArray("outcomes")

                    for (l in 0 until outcomes.length()) {
                        val outcome = outcomes.getJSONObject(l)
                        val line = outcome.optDouble("total", Double.NaN)
                        val opponent = outcome.optString("opponent", "N/A")

                        if (!line.isNaN()) {
                            result.add(
                                BaseballProp(
                                    playerName = playerName,
                                    statType = statType,
                                    line = line,
                                    team = competitorId,
                                    opponent = opponent
                                )
                            )
                        }
                    }
                }
            }
        }

        return result
    }
}

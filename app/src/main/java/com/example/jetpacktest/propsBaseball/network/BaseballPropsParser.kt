package com.example.jetpacktest.propsBaseball.network

import com.example.jetpacktest.propsBaseball.model.BaseballProp
import org.json.JSONArray

object BaseballPropsParser {
    fun parsePropsFromJson(json: String): List<BaseballProp> {
        val propsList = mutableListOf<BaseballProp>()

        val rootArray = JSONArray(json)
        for (i in 0 until rootArray.length()) {
            val event = rootArray.optJSONObject(i) ?: continue
            val homeTeam = event.optString("home_team", "N/A")
            val awayTeam = event.optString("away_team", "N/A")

            val bookmakers = event.optJSONArray("bookmakers") ?: continue
            for (j in 0 until bookmakers.length()) {
                val bookmaker = bookmakers.optJSONObject(j) ?: continue
                val markets = bookmaker.optJSONArray("markets") ?: continue

                for (k in 0 until markets.length()) {
                    val market = markets.optJSONObject(k) ?: continue
                    val statType = market.optString("key", "Unknown")
                    val outcomes = market.optJSONArray("outcomes") ?: continue

                    for (l in 0 until outcomes.length()) {
                        val outcome = outcomes.optJSONObject(l) ?: continue
                        val playerName = outcome.optString("name", "")
                        val line = outcome.optDouble("point", -1.0)

                        if (playerName.isNotEmpty() && line >= 0) {
                            propsList.add(
                                BaseballProp(
                                    playerName = playerName,
                                    statType = statType,
                                    line = line,
                                    team = homeTeam,
                                    opponent = awayTeam
                                )
                            )
                        }
                    }
                }
            }
        }

        return propsList
    }
}

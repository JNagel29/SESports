package com.example.jetpacktest.propsBaseball.sportradar

import org.json.JSONObject

object SportradarPlayerPropsParser {

    fun parsePlayerProps(json: String): List<SportradarPlayerProp> {
        val result = mutableListOf<SportradarPlayerProp>()
        val root = JSONObject(json)

        val players = root.getJSONArray("player_props")

        for (i in 0 until players.length()) {
            val player = players.getJSONObject(i)
            val playerName = player.getString("name")
            val competitorId = player.getString("competitor_id")
            val markets = player.getJSONArray("markets")

            for (j in 0 until markets.length()) {
                val market = markets.getJSONObject(j)
                val statType = market.getString("name")
                val books = market.getJSONArray("books")

                for (k in 0 until books.length()) {
                    val book = books.getJSONObject(k)
                    val sportsbook = book.getString("name")
                    val outcomes = book.getJSONArray("outcomes")

                    val parsedOutcomes = mutableListOf<SportradarOutcome>()

                    for (l in 0 until outcomes.length()) {
                        val outcome = outcomes.getJSONObject(l)
                        val line = outcome.optDouble("total", Double.NaN)
                        val oddsDecimal = outcome.optDouble("odds_decimal", Double.NaN)

                        if (!line.isNaN() && !oddsDecimal.isNaN()) {
                            parsedOutcomes.add(
                                SportradarOutcome(
                                    sportsbook = sportsbook,
                                    line = line,
                                    odds = oddsDecimal.toString()
                                )
                            )
                        }
                    }

                    if (parsedOutcomes.isNotEmpty()) {
                        result.add(
                            SportradarPlayerProp(
                                playerName = playerName,
                                team = competitorId,
                                stat = statType,
                                outcomes = parsedOutcomes
                            )
                        )
                    }
                }
            }
        }

        return result
    }
}

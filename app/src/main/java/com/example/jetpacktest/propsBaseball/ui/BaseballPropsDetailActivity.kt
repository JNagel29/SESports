package com.example.jetpacktest.propsBaseball.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.jetpacktest.ui.theme.JetpackTestTheme

class BaseballPropsDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val eventId = intent.getStringExtra("event_id") ?: ""
        val homeTeam = intent.getStringExtra("home_team")
        val awayTeam = intent.getStringExtra("away_team")

        setContent {
            JetpackTestTheme {
                BaseballOddsScreen(
                    eventId = eventId,
                    homeTeam = homeTeam,
                    awayTeam = awayTeam
                )
            }
        }
    }
}

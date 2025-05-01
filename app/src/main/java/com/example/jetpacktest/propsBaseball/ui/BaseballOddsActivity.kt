package com.example.jetpacktest.propsBaseball.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.jetpacktest.propsBaseball.viewmodel.BaseballOddsViewModel
import com.example.jetpacktest.ui.theme.JetpackTestTheme

class BaseballOddsActivity : ComponentActivity() {

    private val viewModel: BaseballOddsViewModel by viewModels()

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
                    awayTeam = awayTeam,
                    viewModel = viewModel
                )
            }
        }
    }
}

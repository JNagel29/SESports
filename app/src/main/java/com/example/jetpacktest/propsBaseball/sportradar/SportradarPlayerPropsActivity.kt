package com.example.jetpacktest.propsBaseball.sportradar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpacktest.ui.theme.JetpackTestTheme

class SportradarPlayerPropsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val eventId = intent.getStringExtra("event_id")
        val homeTeam = intent.getStringExtra("home_team")
        val awayTeam = intent.getStringExtra("away_team")

        if (eventId == null) {
            finish() // prevent crash if no ID passed
            return
        }

        setContent {
            JetpackTestTheme {
                val vm: SportradarPropsViewModel = viewModel()
                val props by vm.props.collectAsState()
                val error by vm.error.collectAsState()

                LaunchedEffect(eventId) {
                    vm.fetchPlayerProps(eventId, "player_hits") // default stat
                }

                when {
                    error.isNotEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Error: $error", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    props.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    else -> {
                        SportradarPropsScreen(
                            eventId = eventId,
                            homeTeam = homeTeam,
                            awayTeam = awayTeam,
                            viewModel = vm
                        )
                    }
                }
            }
        }
    }
}

package com.example.jetpacktest.props.ui2

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
import com.example.jetpacktest.props.theme.Sportify2Theme
import com.example.jetpacktest.props.viewmodel.PropsUiState
import com.example.jetpacktest.props.viewmodel.PropsViewModel

class PropsDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val eventId = intent.getStringExtra("eventId")
            ?: return finish()

        setContent {
            Sportify2Theme {
                val vm: PropsViewModel = viewModel()
                val uiState by vm.uiState.collectAsState()

                // load once on first composition
                LaunchedEffect(eventId) {
                    vm.loadOdds(eventId)
                }

                when (uiState) {
                    is PropsUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is PropsUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = (uiState as PropsUiState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    is PropsUiState.OddsLoaded -> {
                        // just hand the whole uiState down; PropsDetailScreen will unpack it
                        PropsDetailScreen(
                            uiState   = uiState,
                            onRefresh = { vm.loadOdds(eventId) }
                        )
                    }
                }
            }
        }
    }
}

package com.example.sportify2.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpacktest.props.ui2.OddsScreen
import com.example.jetpacktest.props.viewmodel.OddsViewModel

class OddsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Obtain the same OddsViewModel scoped to this activity
            val oddsViewModel: OddsViewModel = viewModel()

            // Kick off the initial load
            androidx.compose.runtime.LaunchedEffect(Unit) {
                oddsViewModel.fetchOdds()
            }

            // Host the composable screen
            Surface(color = MaterialTheme.colorScheme.background) {
                OddsScreen(oddsViewModel = oddsViewModel)
            }
        }
    }
}

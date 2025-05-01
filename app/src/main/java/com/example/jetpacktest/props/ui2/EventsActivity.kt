// app/src/main/java/com/example/sportify2/ui2/EventsActivity.kt
package com.example.jetpacktest.props.ui2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpacktest.props.theme.Sportify2Theme
import com.example.jetpacktest.props.viewmodel.EventsViewModel

class EventsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Sportify2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // grab our ViewModel & observe state
                    val vm: EventsViewModel = viewModel()
                    val uiState by vm.uiState.collectAsState()
                    val ctx = LocalContext.current

                    EventsScreen(
                        uiState       = uiState,
                        onSelectEvent = { eventId ->
                            ctx.startActivity(
                                Intent(ctx, PropsDetailActivity::class.java)
                                    .putExtra("eventId", eventId)
                            )
                        },
                        onRefresh     = { vm.loadEvents() }
                    )
                }
            }
        }
    }
}

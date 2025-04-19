package com.example.jetpacktest.props.ui2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpacktest.props.network.models.GameOdds
import com.example.jetpacktest.props.viewmodel.OddsUiState
import com.example.jetpacktest.props.viewmodel.OddsViewModel

@Composable
fun OddsScreen(
    oddsViewModel: OddsViewModel = viewModel()
) {
    val uiState by oddsViewModel.oddsState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (uiState) {
            is OddsUiState.Loading -> LoadingView()
            is OddsUiState.Error   -> ErrorView(message = (uiState as OddsUiState.Error).message)
            is OddsUiState.Success -> OddsList(games = (uiState as OddsUiState.Success).data)
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorView(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun OddsList(games: List<GameOdds>) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(games) { game ->
            GameCard(game = game)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GameCard(game: GameOdds) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${game.homeTeam} vs ${game.awayTeam}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start: ${game.commenceTime}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // ─── One block per bookmaker ────────────────────────────
            for (bookmaker in game.bookmakers) {
                Text(
                    text = bookmaker.title,
                    style = MaterialTheme.typography.titleSmall
                )

                // ─── One block per market ─────────────────────────────
                for (market in bookmaker.markets) {
                    Text(
                        text = "Market: ${market.key}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    // ─── One line per outcome ───────────────────────────
                    for (outcome in market.outcomes) {
                        Text(
                            text = buildString {
                                append("${outcome.name}: ${outcome.price}")
                                outcome.point?.let { append(" (Pts: $it)") }
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

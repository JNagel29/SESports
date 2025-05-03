package com.example.jetpacktest.props.ui2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jetpacktest.props.network.models.PlayerOdds
import com.example.jetpacktest.props.util.formatAmerican
import com.example.jetpacktest.props.viewmodel.PropsUiState

@Composable
fun PropsDetailScreen(
    uiState: PropsUiState,
    onRefresh: () -> Unit
) {
    when (uiState) {
        PropsUiState.Loading -> FullScreenSpinner()
        is PropsUiState.Error -> FullScreenError(uiState.message, onRefresh)
        is PropsUiState.OddsLoaded -> OddsTable(uiState.odds, onRefresh)
    }
}

@Composable
private fun FullScreenSpinner() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun FullScreenError(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Error: $message", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun OddsTable(
    odds: List<PlayerOdds>,
    onRefresh: () -> Unit
) {
    // 1) all markets
    val markets = odds.map { it.key }.distinct()

    // 2) the three bookies you want columns for
    val displayedBookies = listOf("DraftKings", "FanDuel", "BetMGM")

    // dropdown state
    var selectedMarket by remember { mutableStateOf(markets.firstOrNull() ?: "") }
    var expanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        // ─── Market dropdown ───────────────────────────────────────
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(if (selectedMarket.isNotEmpty()) selectedMarket else "Select statistic")
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            markets.forEach { mkt ->
                DropdownMenuItem(
                    text = { Text(mkt) },
                    onClick = {
                        selectedMarket = mkt
                        expanded = false
                    }
                )
            }
        }

        // ─── Table header ─────────────────────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)
        ) {
            Text("Player", Modifier.width(100.dp), style = MaterialTheme.typography.titleSmall)
            displayedBookies.forEach { bk ->
                Text(
                    text = bk,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
        Divider()

        // ─── Table body ───────────────────────────────────────────
        val rows = odds
            .filter { it.key == selectedMarket }
            .groupBy { it.playerName }

        LazyColumn(Modifier.weight(1f)) {
            items(rows.entries.toList()) { (player, entries) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(player, Modifier.width(100.dp), style = MaterialTheme.typography.bodyMedium)

                    // one card per bookie
                    displayedBookies.forEach { bk ->
                        // grab the Over/Under for this bookie & player
                        val po = entries.firstOrNull()
                        val over = po
                            ?.selections
                            ?.firstOrNull { it.label == "Over" }
                            ?.books
                            ?.firstOrNull { it.bookie.equals(bk, ignoreCase = true) }
                        val under = po
                            ?.selections
                            ?.firstOrNull { it.label == "Under" }
                            ?.books
                            ?.firstOrNull { it.bookie.equals(bk, ignoreCase = true) }

                        if (over != null || under != null) {
                            // fallback: if no Under threshold, reuse the Over threshold
                            val threshold = over?.line?.line?.toString() ?: "-"
                            val underLine = under?.line?.line?.toString() ?: threshold

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(
                                    Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "o${over?.line?.line ?: "-"}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = formatAmerican(over?.line?.cost ?: 0.0),
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        text = "u$underLine",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = formatAmerican(under?.line?.cost ?: 0.0),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Divider()
            }
        }

        // ─── Refresh ───────────────────────────────────────────────
        Button(
            onClick = onRefresh,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Refresh")
        }
    }
}

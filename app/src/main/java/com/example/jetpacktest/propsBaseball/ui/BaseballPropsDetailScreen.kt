package com.example.jetpacktest.propsBaseball.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jetpacktest.propsBaseball.model.Bookmaker
import com.example.jetpacktest.propsBaseball.model.Market
import com.example.jetpacktest.propsBaseball.model.Outcome

@Composable
fun BaseballPropsDetailScreen(
    homeTeam: String,
    awayTeam: String,
    bookmakers: List<Bookmaker>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "$awayTeam @ $homeTeam",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(bookmakers) { bookmaker ->
                BookmakerCard(bookmaker)
            }
        }
    }
}

@Composable
fun BookmakerCard(bookmaker: Bookmaker) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(bookmaker.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            bookmaker.markets.forEach { market ->
                MarketSection(market)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MarketSection(market: Market) {
    Text(
        text = "Market: ${market.key}",
        style = MaterialTheme.typography.bodyMedium
    )
    market.outcomes.forEach { outcome ->
        OutcomeRow(outcome)
    }
}

@Composable
fun OutcomeRow(outcome: Outcome) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = outcome.name)
        Text(text = "Price: ${outcome.price}")
        outcome.point?.let { point ->
            Text(text = "Point: $point")
        }
    }
}

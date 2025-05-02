package com.example.jetpacktest.propsBaseball.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jetpacktest.ui.theme.JetpackTestTheme

class StaticBaseballPropsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val propsByStat = StaticBaseballPropsData.propsByStat

        setContent {
            JetpackTestTheme {
                var selectedStat by remember { mutableStateOf(propsByStat.keys.first()) }
                var expanded by remember { mutableStateOf(false) }

                Column(Modifier.fillMaxSize()) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(selectedStat)
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
                            propsByStat.keys.forEach { stat ->
                                DropdownMenuItem(
                                    text = { Text(stat) },
                                    onClick = {
                                        selectedStat = stat
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    StatTable(
                        stat = selectedStat,
                        props = propsByStat[selectedStat] ?: emptyList()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { /* No-op for static data */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text("Refresh")
                    }
                }
            }
        }
    }
}

@Composable
fun StatTable(stat: String, props: List<StaticPlayerProp>) {
    val bookies = listOf("DraftKings", "FanDuel", "BetMGM")

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)
        ) {
            Text("Player", Modifier.width(100.dp), style = MaterialTheme.typography.titleSmall)
            bookies.forEach {
                Text(it, Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
            }
        }

        Divider()

        val grouped = props.groupBy { it.player }

        LazyColumn(Modifier.weight(1f)) {
            items(grouped.entries.toList()) { (player, entries) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(player, Modifier.width(100.dp), style = MaterialTheme.typography.bodyMedium)

                    bookies.forEach { bookie ->
                        val selection = entries.firstOrNull()
                        val over = selection?.selections?.firstOrNull { it.label == "Over" }?.books?.firstOrNull { it.bookie == bookie }
                        val under = selection?.selections?.firstOrNull { it.label == "Under" }?.books?.firstOrNull { it.bookie == bookie }

                        if (over != null || under != null) {
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
                                    Text("o${over?.outcome?.line ?: "-"}", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = if ((over?.outcome?.cost ?: 0.0) >= 0) "+${over?.outcome?.cost?.toInt()}" else "${over?.outcome?.cost?.toInt()}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text("u${under?.outcome?.line ?: over?.outcome?.line ?: "-"}", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = if ((under?.outcome?.cost ?: 0.0) >= 0) "+${under?.outcome?.cost?.toInt()}" else "${under?.outcome?.cost?.toInt()}",
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
    }
}

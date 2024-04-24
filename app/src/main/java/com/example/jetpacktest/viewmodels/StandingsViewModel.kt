package com.example.jetpacktest.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.models.TeamStanding
import com.example.jetpacktest.models.TeamStanding.Conference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class StandingsViewModel : ViewModel() {
    private val databaseHandler = DatabaseHandler()

    private val _westernFlow = MutableStateFlow<List<TeamStanding>>(emptyList())
    val westernFlow: Flow<List<TeamStanding>> = _westernFlow

    private val _easternFlow = MutableStateFlow<List<TeamStanding>>(emptyList())
    val easternFlow: Flow<List<TeamStanding>> = _easternFlow

    init {
        fetchWesternStandings()
        fetchEasternStandings()
    }

    private fun fetchWesternStandings() {
        Log.d("StandingsVM", "Fetching Western Standings...")
        databaseHandler.executeStandings(
            conference = Conference.WESTERN,
        ) { teamStandings ->
            _westernFlow.value = teamStandings
        }
    }

    private fun fetchEasternStandings() {
        Log.d("StandingsVM", "Fetching Eastern Standings...")
        databaseHandler.executeStandings(
            conference = Conference.EASTERN
        ) { teamStandings ->
            _easternFlow.value = teamStandings
        }
    }

}
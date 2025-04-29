package com.example.jetpacktest.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.models.BaseballTeamStanding
import com.example.jetpacktest.models.BaseballTeamStanding.League
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class BaseballStandingsViewModel : ViewModel() {
    private val databaseHandler = DatabaseHandler()

    private val _americanFlow = MutableStateFlow<List<BaseballTeamStanding>>(emptyList())
    val americanFlow: Flow<List<BaseballTeamStanding>> = _americanFlow

    private val _nationalFlow = MutableStateFlow<List<BaseballTeamStanding>>(emptyList())
    val nationalFlow: Flow<List<BaseballTeamStanding>> = _nationalFlow

    val yearOptions = listOf("2020", "2021", "2022", "2023", "2024").reversed()

    init {
        updateAmericanStandings("2024")
        updateNationalStandings("2024")
    }

    fun updateAmericanStandings(year: String) {
        Log.d("BaseballStandingsVM", "Fetching American League Standings for $year...")
        databaseHandler.executeBaseballStandings(
            league = League.AMERICAN,
            year = year
        ) { teamStandings ->
            _americanFlow.value = teamStandings
        }
    }

    fun updateNationalStandings(year: String) {
        Log.d("BaseballStandingsVM", "Fetching National League Standings for $year...")
        databaseHandler.executeBaseballStandings(
            league = League.NATIONAL,
            year = year
        ) { teamStandings ->
            _nationalFlow.value = teamStandings
        }
    }
}

package com.example.jetpacktest.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.models.StatLeader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel : ViewModel() {
    private val databaseHandler = DatabaseHandler()

    // Flows for the selected stat and year
    private val _chosenStatFlow = MutableStateFlow("PTS")
    val chosenStatFlow: Flow<String> = _chosenStatFlow

    private val _chosenYearFlow = MutableStateFlow("2024")
    val chosenYearFlow: Flow<String> = _chosenYearFlow

    private val _statLeadersListFlow = MutableStateFlow<List<StatLeader>>(emptyList())
    val statLeadersListFlow: Flow<List<StatLeader>> = _statLeadersListFlow

    fun fetchStatLeaders() {
        val chosenStat = _chosenStatFlow.value
        val chosenYear = _chosenYearFlow.value
        Log.d("HomeVM", "Fetching new stat leads")
        databaseHandler.executeStatLeaders(chosenStat = chosenStat, year = chosenYear) { statLeaders ->
            _statLeadersListFlow.value = statLeaders
        }
    }
    fun updateChosenStat(newStat: String) { _chosenStatFlow.value = newStat }
    fun updateChosenYear(newYear: String) { _chosenYearFlow.value = newYear }
}
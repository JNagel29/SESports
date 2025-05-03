package com.example.jetpacktest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.models.StatLeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BaseballHomeViewModel : ViewModel() {

    private val _chosenYearFlow = MutableStateFlow("2024")
    val chosenYearFlow: StateFlow<String> = _chosenYearFlow.asStateFlow()

    private val _chosenStatFlow = MutableStateFlow("home_runs") // Default stat is HR (home runs)
    val chosenStatFlow: StateFlow<String> = _chosenStatFlow.asStateFlow()

    private val _statLeadersListFlow = MutableStateFlow<List<StatLeader>>(emptyList())
    val statLeadersListFlow: StateFlow<List<StatLeader>> = _statLeadersListFlow.asStateFlow()

    private val databaseHandler = DatabaseHandler()

    fun updateChosenStat(stat: String) {
        _chosenStatFlow.value = stat
    }

    fun updateChosenYear(year: String) {
        _chosenYearFlow.value = year
    }

    fun fetchBaseballLeaders(isPitcher: Boolean, stat: String, year: String) {
        viewModelScope.launch {
            databaseHandler.fetchBaseballStatLeaders(
                isPitcher = isPitcher,
                chosenStat = stat,
                year = year
            ) { leaders ->
                _statLeadersListFlow.value = leaders
            }
        }
    }
}


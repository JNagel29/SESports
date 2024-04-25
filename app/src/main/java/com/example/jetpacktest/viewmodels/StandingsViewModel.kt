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

    val yearOptions = listOf("1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998",
        "1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009",
        "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020",
        "2021", "2022", "2023", "2024").reversed()
    init {
        updateWesternStandings(year = "2024")
        updateEasternStandings(year = "2024")
    }

    fun updateWesternStandings(year: String) {
        Log.d("StandingsVM", "Fetching Western Standings of $year...")
        databaseHandler.executeStandings(
            conference = Conference.WESTERN,
            year = year
        ) { teamStandings ->
            _westernFlow.value = teamStandings
        }
    }

    fun updateEasternStandings(year: String) {
        Log.d("StandingsVM", "Fetching Eastern Standings $year...")
        databaseHandler.executeStandings(
            conference = Conference.EASTERN,
            year = year
        ) { teamStandings ->
            _easternFlow.value = teamStandings
        }
    }

}
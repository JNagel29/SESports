package com.example.jetpacktest.propsBaseball.sportradar

import androidx.compose.runtime.mutableStateOf // ✅ REQUIRED IMPORT
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.net.URL

class SportradarViewModel : ViewModel() {

    var scheduleJson = mutableStateOf<String?>(null)
        private set

    var error = mutableStateOf<String?>(null)
        private set

    fun loadSchedule(date: String) {
        viewModelScope.launch {
            try {
                // Replace this with your actual API call logic
                val json = URL("https://api.sportradar.us/mlb/trial/v7/en/games/$date/schedule.json?api_key=YOUR_KEY").readText()
                scheduleJson.value = json
                error.value = null
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }
}

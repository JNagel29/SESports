// app/src/main/java/com/example/sportify2/viewmodel/EventsViewModel.kt
package com.example.jetpacktest.props.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.props.network.PropsRepository
import com.example.jetpacktest.props.network.models.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EventsUiState {
    object Loading : EventsUiState()
    data class Success(val events: List<Event>) : EventsUiState()
    data class Error(val message: String) : EventsUiState()
}

class EventsViewModel(
    private val repository: PropsRepository = PropsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventsUiState>(EventsUiState.Loading)
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    /**
     * Fetches today’s games (offsetDays = 0).
     * To load yesterday’s or tomorrow’s, pass -1 or +1.
     */
    fun loadEvents() {
        _uiState.value = EventsUiState.Loading
        viewModelScope.launch {
            try {
                val list = repository.todayEvents(offsetDays = 0)
                _uiState.value = EventsUiState.Success(list)
            } catch (t: Throwable) {
                _uiState.value =
                    EventsUiState.Error("Unable to load events: ${t.localizedMessage}")
            }
        }
    }
}

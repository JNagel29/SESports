// app/src/main/java/com/example/sportify2/viewmodel/PropsViewModel.kt
package com.example.jetpacktest.props.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.props.network.PropsRepository
import com.example.jetpacktest.props.network.models.PlayerOdds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PropsUiState {
    object Loading : PropsUiState()
    data class Error(val message: String) : PropsUiState()
    data class OddsLoaded(val odds: List<PlayerOdds>) : PropsUiState()
}

class PropsViewModel(
    private val repo: PropsRepository = PropsRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow<PropsUiState>(PropsUiState.Loading)
    val uiState: StateFlow<PropsUiState> = _uiState.asStateFlow()

    fun loadOdds(eventId: String) {
        _uiState.value = PropsUiState.Loading
        viewModelScope.launch {
            try {
                val odds = repo.playerOdds(eventId)
                _uiState.value = PropsUiState.OddsLoaded(odds)
            } catch (t: Throwable) {
                _uiState.value = PropsUiState.Error("Failed to load props: ${t.localizedMessage}")
            }
        }
    }
}

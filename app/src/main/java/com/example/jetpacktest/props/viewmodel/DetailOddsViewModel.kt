// app/src/main/java/com/example/sportify2/viewmodel/DetailOddsViewModel.kt
package com.example.jetpacktest.props.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.props.network.models.GameOdds
import com.example.jetpacktest.props.network.OddsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DetailOddsUiState {
    object Loading : DetailOddsUiState()
    data class Success(val game: GameOdds) : DetailOddsUiState()
    data class Error(val message: String) : DetailOddsUiState()
}

class DetailOddsViewModel(
    private val repo: OddsRepository = OddsRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow<DetailOddsUiState>(DetailOddsUiState.Loading)
    val uiState: StateFlow<DetailOddsUiState> = _ui.asStateFlow()

    private val apiKey = "4312c01e209032a3e8c465c2910cd5b9"

    fun load(eventId: String) {
        _ui.value = DetailOddsUiState.Loading
        viewModelScope.launch {
            try {
                val game = repo.getOddsForEvent(apiKey, eventId)
                if (game != null) _ui.value = DetailOddsUiState.Success(game)
                else             _ui.value = DetailOddsUiState.Error("No odds for event")
            } catch (t: Throwable) {
                _ui.value = DetailOddsUiState.Error(t.localizedMessage ?: "Unknown error")
            }
        }
    }
}

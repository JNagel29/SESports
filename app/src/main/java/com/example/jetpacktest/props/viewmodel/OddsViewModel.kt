package com.example.jetpacktest.props.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.props.network.OddsRepository
import com.example.jetpacktest.props.network.models.GameOdds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class OddsUiState {
    object Loading : OddsUiState()
    data class Success(val data: List<GameOdds>) : OddsUiState()
    data class Error(val message: String) : OddsUiState()
}

class OddsViewModel(
    private val repository: OddsRepository = OddsRepository()
) : ViewModel() {

    private val _oddsState = MutableStateFlow<OddsUiState>(OddsUiState.Loading)
    val oddsState: StateFlow<OddsUiState> = _oddsState.asStateFlow()

    private val apiKey = "4312c01e209032a3e8c465c2910cd5b9"

    init {
        fetchOdds()
    }

    fun fetchOdds() {
        _oddsState.value = OddsUiState.Loading
        viewModelScope.launch {
            try {
                val list = repository.getUpcomingOdds(apiKey)
                _oddsState.value = OddsUiState.Success(list)
            } catch (e: Exception) {
                _oddsState.value = OddsUiState.Error("Failed to load odds: ${e.localizedMessage}")
            }
        }
    }
}

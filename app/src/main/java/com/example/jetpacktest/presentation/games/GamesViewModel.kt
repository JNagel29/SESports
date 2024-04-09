package com.example.jetpacktest.presentation.games

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.common.Resource
import com.example.jetpacktest.domain.model.Games
import com.example.jetpacktest.domain.use_case.GamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class GamesViewModel @Inject constructor(
    private val gamesUseCase: GamesUseCase
): ViewModel() {

    private val _state = mutableStateOf(GamesState())
    val state: State<GamesState> = _state

    init {
        fetchGames(Date())
    }
    fun fetchGames(date: Date) {
        gamesUseCase(date = date).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = GamesState(games = (result.data ?: Games(emptyList())))
                }
                is Resource.Error -> {
                    _state.value = GamesState(error = result.message ?: "Unexpected error")
                }
                is Resource.Loading -> {
                    _state.value = GamesState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}
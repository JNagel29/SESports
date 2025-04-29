package com.example.jetpacktest.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.DatabaseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class BaseballSearchViewModel : ViewModel() {

    private val databaseHandler = DatabaseHandler()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _selectedSearchType = MutableStateFlow("Player")
    val selectedSearchType: StateFlow<String> = _selectedSearchType.asStateFlow()

    private val _playerResults = MutableStateFlow<List<String>>(emptyList())
    val playerResults: StateFlow<List<String>> = searchText
        .debounce(500L)
        .combine(_selectedSearchType) { text, searchType ->
            searchType to text
        }
        .distinctUntilChanged()
        .onEach { (searchType, text) ->
            if (searchType == "Player" && text.isNotBlank()) {
                _isSearching.value = true
                databaseHandler.executeBaseballPlayerSearch(text) { results ->
                    _playerResults.value = results
                    _isSearching.value = false
                }
            } else {
                _playerResults.value = emptyList()
            }
        }
        .map { _playerResults.value }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // You can uncomment and implement this later
    // private val _teamResults = MutableStateFlow<List<String>>(emptyList())

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onSearchTypeChange(type: String) {
        _selectedSearchType.value = type
        clearResults()
    }

    fun clearResults() {
        _playerResults.value = emptyList()
        // _teamResults.value = emptyList()
    }
}
package com.example.jetpacktest.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.RestHandler
import com.example.jetpacktest.models.TeamMaps
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


@OptIn(FlowPreview::class)
class SearchViewModel() : ViewModel() {

    private val databaseHandler = DatabaseHandler()
    private val restHandler = RestHandler()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _selectedSearchType = MutableStateFlow("Player")
    val selectedSearchType = _selectedSearchType.asStateFlow()

    private val _playerResults = MutableStateFlow<List<String>>(emptyList())

    //Niko: Used this video as a guide: https://www.youtube.com/watch?v=CfL6Dl2_dAE
    @OptIn(ExperimentalCoroutinesApi::class)
    val playerResults = combine(
        _selectedSearchType,
        searchText.debounce(500L)
    ) { searchType, text ->
        if (searchType == "Player" && text.isNotBlank()) {
            _isSearching.value = true
            viewModelScope.launch {
                databaseHandler.executePlayerSearchResults(text) { results ->
                    _playerResults.value = results
                    _isSearching.value = false
                }
            }
            _playerResults
        } else {
            flowOf(emptyList())
        }
    }.flattenMerge()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    /* TODO: Was testing connecting via REST API and it worked, will implement more l8r
    @OptIn(ExperimentalCoroutinesApi::class)
    val playerResults = combine(
        _selectedSearchType,
        searchText.debounce(500L)
    ) { searchType, text ->
        if (searchType == "Player" && text.isNotBlank()) {
            _isSearching.value = true
            viewModelScope.launch {
                try {
                    _playerResults.value = restHandler.getPlayerSearchResults(text)
                } catch (e: SocketTimeoutException) {
                    Log.e("SearchViewModel", "ERROR: ${e.message}. Check Server")
                    _playerResults.value = emptyList()
                }
                finally {
                    _isSearching.value = false
                }
            }
            _playerResults
        } else {
            flowOf(emptyList())
        }
    }.flattenMerge()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

     */


    private val _teamResults = MutableStateFlow<List<String>>(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val teamResults = searchText
        .flatMapLatest { text ->
            if (text.isBlank()) {
                flowOf(TeamMaps.names)
            } else {
                flow {
                    val filteredTeams = TeamMaps.names.filter { teamName ->
                        teamName.contains(text, ignoreCase = true)
                    }
                    emit(filteredTeams)
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onSearchTypeChanged(text: String) {
        _selectedSearchType.value = text
    }

    fun clearResults() {
        _playerResults.value = emptyList()
        _teamResults.value = emptyList()
    }

}
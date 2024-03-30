import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.DatabaseHandler
import com.example.jetpacktest.models.NbaTeam
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


@OptIn(FlowPreview::class)
class SearchViewModel() : ViewModel() {

    private val databaseHandler = DatabaseHandler()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _selectedSearchType = MutableStateFlow("Player")
    val selectedSearchType = _selectedSearchType.asStateFlow()

    private val _playerResults = MutableStateFlow<List<String>>(emptyList())

    //Niko: Used this video as a guide: https://www.youtube.com/watch?v=CfL6Dl2_dAE
    @OptIn(ExperimentalCoroutinesApi::class)
    val playerResults = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .flatMapLatest { text ->
            if (text.isBlank()) {
                flowOf(_playerResults.value)
            } else {
                viewModelScope.launch {
                    databaseHandler.executePlayerSearchResults(text) { results ->
                        _playerResults.value = results
                        _isSearching.value = false //Update isSearching after the search is complete
                    }
                }
                _playerResults
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _playerResults.value
        )

    private val _teamResults = MutableStateFlow<List<String>>(emptyList())

    //TODO: need to figure out how to not search for players when searching team
    @OptIn(ExperimentalCoroutinesApi::class)
    val teamResults = searchText
        .flatMapLatest { text ->
            if (text.isBlank()) {
                flowOf(emptyList())
            } else {
                flow {
                    val filteredTeams = NbaTeam.names.filter { teamName ->
                        teamName.contains(text, ignoreCase = true)
                    }
                    emit(filteredTeams)
                }
            }
        }
        .onEach { _isSearching.value = it.isNotEmpty() }
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
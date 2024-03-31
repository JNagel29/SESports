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

    private val _teamResults = MutableStateFlow<List<String>>(emptyList())

    //TODO: need to figure out how to not search for players when searching team
    @OptIn(ExperimentalCoroutinesApi::class)
    val teamResults = searchText
        .flatMapLatest { text ->
            if (text.isBlank()) {
                flowOf(NbaTeam.names)
            } else {
                flow {
                    val filteredTeams = NbaTeam.names.filter { teamName ->
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
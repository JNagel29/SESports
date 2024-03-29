import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpacktest.DatabaseHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


@OptIn(FlowPreview::class)
class SearchViewModel() : ViewModel() {

    private val databaseHandler = DatabaseHandler()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _playerResults = MutableStateFlow<List<String>>(emptyList())

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
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun clearPlayerResults() {
        _playerResults.value = emptyList()
    }

}
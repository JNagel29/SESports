package com.example.jetpacktest.propsBaseball.sportradar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SportradarPropsViewModel : ViewModel() {

    private val api = SportradarRetrofitClient.instance
    private val repository = SportradarPlayerPropsRepository(api)

    private val _props = MutableStateFlow<List<SportradarPlayerProp>>(emptyList())
    val props: StateFlow<List<SportradarPlayerProp>> = _props

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun fetchPlayerProps(eventId: String, statType: String) {
        repository.getPlayerProps(
            sportEventId = eventId,
            onSuccess = { json ->
                val allProps = SportradarPlayerPropsParser.parsePlayerProps(json)
                _props.value = allProps.filter { it.stat.equals(statType, ignoreCase = true) }
                _error.value = ""
            },
            onError = { msg ->
                _props.value = emptyList()
                _error.value = msg
            }
        )
    }
}

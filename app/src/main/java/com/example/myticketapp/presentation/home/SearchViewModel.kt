package com.example.myticketapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.domain.model.Event
import com.example.myticketapp.domain.repository.HomeRepository
import com.example.myticketapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val isLoading: Boolean = false,
    val results: List<Event> = emptyList(),
    val error: String = ""
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    init {
        _searchQuery
            .debounce(500L)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isBlank()) {
                    _state.value = SearchState(results = emptyList())
                } else {
                    performSearch(query)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun performSearch(keyword: String) {
        viewModelScope.launch {
            repository.searchEvents(keyword).onEach { result ->
                when (result) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true, error = "")
                    is Resource.Success -> _state.value = SearchState(
                        isLoading = false,
                        results = result.data ?: emptyList()
                    )
                    is Resource.Error -> _state.value = SearchState(
                        isLoading = false,
                        error = result.message ?: "Lỗi",
                        results = emptyList()
                    )
                }
            }.launchIn(viewModelScope)
        }
    }
}

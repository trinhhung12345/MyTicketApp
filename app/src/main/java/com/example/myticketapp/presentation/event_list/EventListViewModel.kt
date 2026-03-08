package com.example.myticketapp.presentation.event_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.domain.model.Event
import com.example.myticketapp.domain.repository.HomeRepository
import com.example.myticketapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.net.URLDecoder
import javax.inject.Inject

data class EventListState(
    val isLoading: Boolean = true,
    val events: List<Event> = emptyList(),
    val error: String = ""
)

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val repository: HomeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(EventListState())
    val state: State<EventListState> = _state

    // Lấy Tên danh mục từ Navigation để làm Title màn hình
    val categoryName: String = URLDecoder.decode(savedStateHandle.get<String>("categoryName") ?: "Sự kiện", "UTF-8")

    init {
        val categoryId = savedStateHandle.get<Int>("categoryId") ?: 0
        fetchEvents(categoryId)
    }

    private fun fetchEvents(categoryId: Int) {
        val flow = if (categoryId == 0) {
            repository.getEvents() // Lấy tất cả
        } else {
            repository.getEventsByCategory(categoryId) // Lấy theo ID
        }

        flow.onEach { result ->
            when (result) {
                is Resource.Loading -> _state.value = EventListState(isLoading = true)
                is Resource.Success -> _state.value = EventListState(isLoading = false, events = result.data ?: emptyList())
                is Resource.Error -> _state.value = EventListState(isLoading = false, error = result.message ?: "Lỗi")
            }
        }.launchIn(viewModelScope)
    }
}
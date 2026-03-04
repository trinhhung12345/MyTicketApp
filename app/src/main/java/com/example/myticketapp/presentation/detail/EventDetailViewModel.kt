package com.example.myticketapp.presentation.detail

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
import javax.inject.Inject

data class EventDetailState(
    val isLoading: Boolean = true,
    val event: Event? = null,
    val isTokenExpired: Boolean = false, // Cờ báo hiệu token chết
    val error: String = ""
)

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val repository: HomeRepository,
    savedStateHandle: SavedStateHandle // Dùng để lấy tham số truyền trên URL
) : ViewModel() {

    private val _state = mutableStateOf(EventDetailState())
    val state: State<EventDetailState> = _state

    init {
        // Tự động lấy "eventId" truyền từ màn Home
        val eventId = savedStateHandle.get<Int>("eventId")
        if (eventId != null) {
            getEventDetail(eventId)
        } else {
            _state.value = EventDetailState(isLoading = false, error = "Không tìm thấy ID sự kiện")
        }
    }

    private fun getEventDetail(id: Int) {
        repository.getEventDetail(id).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = EventDetailState(isLoading = true)
                }
                is Resource.Success -> {
                    _state.value = EventDetailState(isLoading = false, event = result.data)
                }
                is Resource.Error -> {
                    if (result.message == "TOKEN_EXPIRED") {
                        _state.value = EventDetailState(isLoading = false, isTokenExpired = true)
                    } else {
                        _state.value = EventDetailState(isLoading = false, error = result.message ?: "Lỗi tải chi tiết")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}

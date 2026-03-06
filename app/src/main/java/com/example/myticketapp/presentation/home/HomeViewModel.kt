package com.example.myticketapp.presentation.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.domain.model.Category
import com.example.myticketapp.domain.model.Event
import com.example.myticketapp.domain.repository.HomeRepository
import com.example.myticketapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel"

data class HomeState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val categories: List<Category> = emptyList(),
    val featuredEvents: List<Event> = emptyList(), // Nổi bật (Banner)
    val specialEvents: List<Event> = emptyList(),  // Đặc sắc (Grid)
    val eventsByCategory: Map<String, List<Event>> = emptyMap(), // Dùng cho các danh sách ngang phía dưới
    val error: String = "",
    val showSessionExpiredDialog: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private var pollingJob: Job? = null

    init {
        fetchCategories()
        fetchEvents()
        startPolling()
    }

    private fun startPolling() {
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(30000) // 30 seconds
                refreshHome()
            }
        }
    }

    private fun fetchCategories() {
        repository.getCategories().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    // Không làm gì khi loading
                }
                is Resource.Success -> {
                    // Thêm chữ "Tất cả" vào đầu list category cho UI
                    val allCats = listOf(Category(0, "Tất cả")) + (result.data ?: emptyList())
                    _state.value = _state.value.copy(categories = allCats)
                }
                is Resource.Error -> {
                    Log.d(TAG, "fetchCategories error: ${result.message}")
                    if (result.message == "TOKEN_EXPIRED") {
                        Log.d(TAG, "fetchCategories: Setting showSessionExpiredDialog = true")
                        _state.value = _state.value.copy(
                            showSessionExpiredDialog = true
                        )
                        // Hủy polling để tránh gọi lại liên tục khi token hết hạn
                        pollingJob?.cancel()
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchEvents(showLoading: Boolean = true) {
        if (showLoading) {
            _state.value = _state.value.copy(isLoading = true)
        }
        repository.getEvents().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    if (showLoading) {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                }
                is Resource.Success -> {
                    val allEvents = result.data ?: emptyList()

                    // --- LOGIC PHÂN LOẠI FRONT-END ---
                    // 1. Nổi bật: Lấy 2 event đầu tiên
                    val featured = allEvents.take(2)

                    // 2. Đặc sắc: Bỏ qua 2 cái đầu, lấy 4 cái tiếp theo
                    val special = allEvents.drop(2).take(4)

                    // 3. Phân nhóm theo CategoryName (Ví dụ: POP có 3 sự kiện, Rock có 2 sự kiện...)
                    val byCategory = allEvents.groupBy { it.categoryName }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        featuredEvents = featured,
                        specialEvents = special,
                        eventsByCategory = byCategory
                    )
                }
                is Resource.Error -> {
                    Log.d(TAG, "fetchEvents error: ${result.message}")
                    if (result.message == "TOKEN_EXPIRED") {
                        Log.d(TAG, "fetchEvents: Setting showSessionExpiredDialog = true")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            showSessionExpiredDialog = true
                        )
                        // Hủy polling để tránh gọi lại liên tục khi token hết hạn
                        pollingJob?.cancel()
                    } else {
                        _state.value = _state.value.copy(isLoading = false, isRefreshing = false, error = result.message ?: "Lỗi")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun refreshHome() {
        _state.value = _state.value.copy(isRefreshing = true)
        repository.getEvents().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    // Không cập nhật isLoading khi refresh, chỉ giữ isRefreshing
                }
                is Resource.Success -> {
                    val allEvents = result.data ?: emptyList()

                    // --- LOGIC PHÂN LOẠI FRONT-END ---
                    // 1. Nổi bật: Lấy 2 event đầu tiên
                    val featured = allEvents.take(2)

                    // 2. Đặc sắc: Bỏ qua 2 cái đầu, lấy 4 cái tiếp theo
                    val special = allEvents.drop(2).take(4)

                    // 3. Phân nhóm theo CategoryName (Ví dụ: POP có 3 sự kiện, Rock có 2 sự kiện...)
                    val byCategory = allEvents.groupBy { it.categoryName }

                    _state.value = _state.value.copy(
                        isRefreshing = false,
                        featuredEvents = featured,
                        specialEvents = special,
                        eventsByCategory = byCategory
                    )
                }
                is Resource.Error -> {
                    Log.d(TAG, "refreshHome error: ${result.message}")
                    if (result.message == "TOKEN_EXPIRED") {
                        Log.d(TAG, "refreshHome: Setting showSessionExpiredDialog = true")
                        _state.value = _state.value.copy(
                            isRefreshing = false,
                            showSessionExpiredDialog = true
                        )
                        // Hủy polling để tránh gọi lại liên tục khi token hết hạn
                        pollingJob?.cancel()
                    } else {
                        _state.value = _state.value.copy(isRefreshing = false, error = result.message ?: "Lỗi")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onSessionExpiredConfirmed() {
        _state.value = _state.value.copy(showSessionExpiredDialog = false)
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}

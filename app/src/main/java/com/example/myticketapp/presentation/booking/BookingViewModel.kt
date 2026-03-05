package com.example.myticketapp.presentation.booking

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.domain.model.Seat
import com.example.myticketapp.domain.model.SeatMap
import com.example.myticketapp.domain.model.Section
import com.example.myticketapp.domain.repository.BookingRepository
import com.example.myticketapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class BookingState(
    val isLoading: Boolean = true,
    val seatMap: SeatMap? = null,
    val selectedSeats: List<Seat> = emptyList(),
    val currentSectionId: Int? = null, // Lưu ID của khu vực đang chọn
    val totalPrice: Long = 0L,
    val error: String = "",
    val uiEvent: String = "" // Dùng để bắn Toast (vd: "Chỉ được chọn 1 khu vực")
)

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val repository: BookingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(BookingState())
    val state: State<BookingState> = _state

    // Tạm thời hardcode data sự kiện cho Header (sau này có thể truyền qua NavArgument)
    val eventName = "TOKYO GIRLS COLLECTION VIETNAM 2026..."
    val showingTime = "Suất diễn: 2026-03-29 18:35"

    init {
        val showingId = savedStateHandle.get<Int>("showingId") ?: 4 // Default là 4 để test
        fetchSeatMap(showingId)
    }

    private fun fetchSeatMap(showingId: Int) {
        repository.getSeatMap(showingId).onEach { result ->
            when (result) {
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                is Resource.Success -> {
                    // API trả về List, ta lấy phần tử đầu tiên
                    _state.value = _state.value.copy(isLoading = false, seatMap = result.data)
                }
                is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message ?: "Lỗi")
            }
        }.launchIn(viewModelScope)
    }

    fun toggleSeat(seat: Seat, section: Section) {
        val currentState = _state.value
        val currentList = currentState.selectedSeats.toMutableList()

        if (currentList.isEmpty()) {
            // Chọn ghế đầu tiên -> Khóa sectionId lại
            currentList.add(seat)
            updateCartState(currentList, section.id)
        } else {
            // Đã có ghế trong giỏ -> Kiểm tra Section
            if (currentState.currentSectionId != section.id) {
                // VI PHẠM RULE: Bắn thông báo
                _state.value = currentState.copy(uiEvent = "Bạn chỉ được chọn ghế trong khu vực đang chọn")
                return
            }

            // Cùng section -> Thêm hoặc Bỏ
            if (currentList.any { it.id == seat.id }) {
                currentList.removeAll { it.id == seat.id }
            } else {
                // MỚI THÊM: Kiểm tra giới hạn mua vé (maxQtyPerOrder) từ API
                if (currentList.size >= section.maxQtyPerOrder) {
                    _state.value = currentState.copy(uiEvent = "Bạn chỉ được mua tối đa ${section.maxQtyPerOrder} vé cho khu vực này!")
                    return
                }
                // Thỏa mãn thì thêm ghế
                currentList.add(seat)
            }
            
            val newSectionId = if (currentList.isEmpty()) null else section.id
            updateCartState(currentList, newSectionId)
        }
    }

    fun removeSeat(seat: Seat) {
        val currentList = _state.value.selectedSeats.toMutableList()
        currentList.removeAll { it.id == seat.id }
        val newSectionId = if (currentList.isEmpty()) null else _state.value.currentSectionId
        updateCartState(currentList, newSectionId)
    }

    private fun updateCartState(seats: List<Seat>, sectionId: Int?) {
        val total = seats.sumOf { it.price }
        _state.value = _state.value.copy(
            selectedSeats = seats,
            currentSectionId = sectionId,
            totalPrice = total,
            uiEvent = "" // Clear event cũ
        )
    }

    fun clearUiEvent() {
        _state.value = _state.value.copy(uiEvent = "")
    }
}
package com.example.myticketapp.presentation.booking

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.domain.model.Seat
import com.example.myticketapp.domain.model.SeatMap
import com.example.myticketapp.domain.model.Section
import com.example.myticketapp.domain.model.TicketType
import com.example.myticketapp.domain.repository.BookingRepository
import com.example.myticketapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class BookingState(
    val isLoading: Boolean = true,
    
    // Data gốc
    val seatMap: SeatMap? = null,
    val ticketTypes: List<TicketType> = emptyList(),
    val hasSeatMap: Boolean = false, // Cờ để UI biết phải vẽ cái gì
    
    // State cho chế độ SeatMap (Như cũ)
    val selectedSeats: List<Seat> = emptyList(),
    val currentSectionId: Int? = null,
    
    // State cho chế độ TicketList (Không sơ đồ)
    val ticketQuantities: Map<Int, Int> = emptyMap(), // Lưu: TicketTypeId -> Số lượng
    val totalTickets: Int = 0,
    
    val totalPrice: Long = 0L,
    val error: String = "",
    val uiEvent: String = ""
)

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val repository: BookingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(BookingState())
    val state: State<BookingState> = _state

    init {
        val showingId = savedStateHandle.get<Int>("showingId") ?: 4 // Default là 4 để test
        fetchBookingData(showingId)
    }

    private fun fetchBookingData(showingId: Int) {
        repository.getBookingData(showingId).onEach { result ->
            when (result) {
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                is Resource.Success -> {
                    val data = result.data
                    _state.value = _state.value.copy(
                        isLoading = false,
                        seatMap = data?.seatMap,
                        ticketTypes = data?.ticketTypes ?: emptyList(),
                        hasSeatMap = data?.seatMap != null // Đặt cờ
                    )
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

    // Hàm mới: Xử lý nút [+] và [-]
    fun updateTicketQuantity(ticketType: TicketType, delta: Int) {
        val currentState = _state.value
        val currentQty = currentState.ticketQuantities[ticketType.id] ?: 0
        val newQty = currentQty + delta

        // Chặn số lượng âm
        if (newQty < 0) return

        // Chặn giới hạn mua tối đa (maxQtyPerOrder) và vé tồn kho
        if (delta > 0) {
            if (newQty > ticketType.maxQtyPerOrder) {
                _state.value = currentState.copy(uiEvent = "Bạn chỉ được mua tối đa ${ticketType.maxQtyPerOrder} vé loại này!")
                return
            }
            if (newQty > ticketType.remainingQuantity) {
                _state.value = currentState.copy(uiEvent = "Loại vé này chỉ còn ${ticketType.remainingQuantity} vé!")
                return
            }
        }

        // Cập nhật Map
        val newMap = currentState.ticketQuantities.toMutableMap()
        if (newQty == 0) {
            newMap.remove(ticketType.id)
        } else {
            newMap[ticketType.id] = newQty
        }

        // Tính lại tổng tiền và tổng vé
        var total = 0L
        var count = 0
        newMap.forEach { (id, qty) ->
            val type = currentState.ticketTypes.find { it.id == id }
            total += (type?.price ?: 0L) * qty
            count += qty
        }

        _state.value = currentState.copy(
            ticketQuantities = newMap,
            totalPrice = total,
            totalTickets = count
        )
    }

    fun clearUiEvent() {
        _state.value = _state.value.copy(uiEvent = "")
    }
}

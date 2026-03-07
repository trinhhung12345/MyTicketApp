package com.example.myticketapp.presentation.tickets

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.domain.model.Order
import com.example.myticketapp.domain.repository.OrderRepository
import com.example.myticketapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class OrderDetailState(
    val isLoading: Boolean = true,
    val order: Order? = null,
    val error: String = ""
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val repository: OrderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(OrderDetailState())
    val state: State<OrderDetailState> = _state

    private val orderId: Int? = savedStateHandle.get<Int>("orderId")

    init {
        val id = orderId
        if (id == null) {
            _state.value = OrderDetailState(isLoading = false, error = "Không tìm thấy mã đơn hàng")
        } else {
            loadOrder(id)
        }
    }

    private fun loadOrder(id: Int) {
        repository.getOrderById(id).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true, error = "")
                }

                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        order = result.data,
                        error = ""
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Lỗi tải chi tiết đơn hàng"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun markAsCancelled() {
        val current = _state.value.order ?: return
        if (current.status != "UNPAID") return

        _state.value = _state.value.copy(
            order = current.copy(status = "CANCELLED")
        )
    }
}

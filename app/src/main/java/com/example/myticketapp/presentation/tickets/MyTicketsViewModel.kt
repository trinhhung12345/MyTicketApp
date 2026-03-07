package com.example.myticketapp.presentation.tickets

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.domain.model.Order
import com.example.myticketapp.domain.repository.OrderRepository
import com.example.myticketapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class MyTicketsState(
    val isLoading: Boolean = true,
    val orders: List<Order> = emptyList(),
    val error: String = ""
)

@HiltViewModel
class MyTicketsViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {

    private val _state = mutableStateOf(MyTicketsState())
    val state: State<MyTicketsState> = _state

    init {
        refresh()
    }

    fun refresh() {
        repository.getMyOrders().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true, error = "")
                }

                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        orders = result.data.orEmpty(),
                        error = ""
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Lỗi tải danh sách đơn hàng"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}

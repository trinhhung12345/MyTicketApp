package com.example.myticketapp.presentation.checkout

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.data.local.TokenDataStore
import com.example.myticketapp.domain.model.CheckoutCart
import com.example.myticketapp.domain.model.CreateOrderPayload
import com.example.myticketapp.domain.model.Order
import com.example.myticketapp.domain.repository.OrderRepository
import com.example.myticketapp.domain.repository.UserRepository
import com.example.myticketapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutState(
    val cart: CheckoutCart? = null,
    val totalTickets: Int = 0,
    val totalPrice: Long = 0L,
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val isLoading: Boolean = true,
    val isCreatingOrder: Boolean = false,
    val isCheckingOut: Boolean = false,
    val screenError: String = "",
    val errorMessage: String = "",
    val createdOrder: Order? = null,
    val payosUrl: String? = null,
    val uiEvent: String = ""
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenDataStore: TokenDataStore,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _state = mutableStateOf(CheckoutState())
    val state: State<CheckoutState> = _state

    init {
        loadUserInfo()
    }

    fun initCart(cart: CheckoutCart) {
        if (_state.value.cart != null) return

        val totalQty: Int
        val totalAmount: Long

        if (cart.hasSeatMap) {
            totalQty = cart.selectedSeats.size
            totalAmount = cart.selectedSeats.sumOf { it.price }
        } else {
            totalQty = cart.selectedTickets.sumOf { it.quantity }
            totalAmount = cart.selectedTickets.sumOf { it.price * it.quantity }
        }

        _state.value = _state.value.copy(
            cart = cart,
            totalTickets = totalQty,
            totalPrice = totalAmount,
            isLoading = false,
            screenError = ""
        )
    }

    fun setCartError(message: String) {
        _state.value = _state.value.copy(
            isLoading = false,
            screenError = message
        )
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            val savedAddress = tokenDataStore.userAddress.first()
            _state.value = _state.value.copy(address = savedAddress)

            userRepository.getMyProfile().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val profile = result.data ?: return@collect
                        _state.value = _state.value.copy(
                            name = profile.name,
                            phone = profile.phone,
                            email = profile.email,
                            address = _state.value.address.ifBlank { savedAddress }
                        )
                    }

                    is Resource.Error -> {
                        if (_state.value.cart != null) {
                            _state.value = _state.value.copy(isLoading = false)
                        }
                    }

                    is Resource.Loading -> Unit
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _state.value = _state.value.copy(name = value)
    }

    fun onPhoneChange(value: String) {
        _state.value = _state.value.copy(phone = value)
    }

    fun onEmailChange(value: String) {
        _state.value = _state.value.copy(email = value)
    }

    fun onAddressChange(value: String) {
        _state.value = _state.value.copy(address = value)
    }

    fun createOrder() {
        val currentState = _state.value
        val cart = currentState.cart ?: run {
            _state.value = currentState.copy(errorMessage = "Không tìm thấy dữ liệu thanh toán")
            return
        }

        if (currentState.name.isBlank() || currentState.phone.isBlank() || currentState.email.isBlank()) {
            _state.value = currentState.copy(errorMessage = "Vui lòng nhập đầy đủ họ tên, số điện thoại và email")
            return
        }

        if (!cart.hasSeatMap && cart.selectedTickets.size != 1) {
            _state.value = currentState.copy(
                errorMessage = "Hiện tại hệ thống chỉ hỗ trợ thanh toán một loại vé cho mỗi đơn hàng không sơ đồ ghế"
            )
            return
        }

        val firstTicket = cart.selectedTickets.firstOrNull()
        val payload = CreateOrderPayload(
            recipientName = currentState.name.trim(),
            recipientPhone = currentState.phone.trim(),
            recipientEmail = currentState.email.trim(),
            recipientAddress = currentState.address.trim(),
            seatIds = cart.selectedSeats.takeIf { cart.hasSeatMap }?.map { it.id },
            ticketTypeId = firstTicket?.ticketTypeId,
            quantity = firstTicket?.quantity
        )

        orderRepository.createOrder(payload).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        isCreatingOrder = true,
                        errorMessage = "",
                        payosUrl = null
                    )
                }

                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isCreatingOrder = false,
                        createdOrder = result.data,
                        errorMessage = "",
                        uiEvent = "Tạo đơn hàng thành công"
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isCreatingOrder = false,
                        errorMessage = result.message.orEmpty()
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun processPayment() {
        val orderId = _state.value.createdOrder?.id ?: return

        orderRepository.checkout(orderId).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        isCheckingOut = true,
                        errorMessage = ""
                    )
                }

                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isCheckingOut = false,
                        payosUrl = result.data,
                        errorMessage = ""
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isCheckingOut = false,
                        errorMessage = result.message.orEmpty()
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun markOrderAsExpired() {
        val currentOrder = _state.value.createdOrder ?: return
        if (currentOrder.status != "UNPAID") return

        _state.value = _state.value.copy(
            createdOrder = currentOrder.copy(status = "CANCELLED"),
            errorMessage = "Đơn hàng đã bị hủy do hết thời gian thanh toán. Vui lòng đặt lại vé.",
            payosUrl = null
        )
    }

    fun onPayosUrlOpened() {
        _state.value = _state.value.copy(payosUrl = null)
    }

    fun clearUiEvent() {
        _state.value = _state.value.copy(uiEvent = "")
    }
}
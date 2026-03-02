package com.example.myticketapp.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.domain.model.RegisterRequest
import com.example.myticketapp.domain.usecase.RegisterUseCase
import com.example.myticketapp.domain.usecase.SendOtpUseCase
import com.example.myticketapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState(
    val isLoading: Boolean = false,
    val showOtpModal: Boolean = false,
    val otpTimer: Int = 60,
    val error: String = "",
    val isSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val sendOtpUseCase: SendOtpUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = mutableStateOf(RegisterState())
    val state: State<RegisterState> = _state

    // Lưu trữ thông tin form để dùng khi gọi API register
    private var currentForm: RegisterRequest? = null
    private var timerJob: Job? = null

    // Bước 1: Validate & Gửi OTP
    fun onRegisterClick(
        name: String,
        phone: String,
        email: String,
        pass: String,
        confirmPass: String
    ) {
        if (name.isBlank() || phone.isBlank() || email.isBlank() || pass.isBlank()) {
            _state.value = _state.value.copy(error = "Vui lòng nhập đầy đủ thông tin")
            return
        }
        if (pass != confirmPass) {
            _state.value = _state.value.copy(error = "Mật khẩu xác nhận không khớp")
            return
        }

        // Lưu tạm form data
        currentForm = RegisterRequest(
            accountName = name,
            accountPhone = phone,
            email = email,
            password = pass,
            confirmPassword = confirmPass,
            code = ""
        )

        sendOtp(phone, email)
    }

    fun sendOtp(phone: String, email: String) {
        sendOtpUseCase(phone, email).onEach { result ->
            when (result) {
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true, error = "")
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, showOtpModal = true)
                    startTimer()
                }
                is Resource.Error -> _state.value =
                    _state.value.copy(isLoading = false, error = result.message ?: "Lỗi gửi OTP")
            }
        }.launchIn(viewModelScope)
    }

    // Bước 2: Nhận OTP từ UI và gọi Đăng Ký
    fun verifyOtpAndRegister(otpCode: String) {
        val form = currentForm ?: return
        if (otpCode.length < 6) {
            _state.value = _state.value.copy(error = "Vui lòng nhập đủ 6 số OTP")
            return
        }

        val finalRequest = form.copy(code = otpCode)

        registerUseCase(finalRequest).onEach { result ->
            when (result) {
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true, error = "")
                is Resource.Success -> {
                    _state.value =
                        _state.value.copy(isLoading = false, isSuccess = true, showOtpModal = false)
                    timerJob?.cancel()
                }
                is Resource.Error -> _state.value =
                    _state.value.copy(isLoading = false, error = result.message ?: "Lỗi đăng ký")
            }
        }.launchIn(viewModelScope)
    }

    private fun startTimer() {
        timerJob?.cancel()
        _state.value = _state.value.copy(otpTimer = 60)
        timerJob = viewModelScope.launch {
            while (_state.value.otpTimer > 0) {
                delay(1000L)
                _state.value = _state.value.copy(otpTimer = _state.value.otpTimer - 1)
            }
        }
    }

    fun closeOtpModal() {
        _state.value = _state.value.copy(showOtpModal = false)
        timerJob?.cancel()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = "")
    }
}

package com.example.myticketapp.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.domain.repository.AuthRepository
import com.example.myticketapp.domain.usecase.LoginUseCase
import com.example.myticketapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

// State bọc trạng thái của màn hình Login
data class LoginState(
    val isLoading: Boolean = false,
    val error: String = "",
    val isSuccess: Boolean = false,
    val successMessage: String = "", // Hoặc lưu Token ở đây
    val isForgotPasswordLoading: Boolean = false,
    val forgotPasswordCooldownSeconds: Int = 0,
    val forgotPasswordError: String = "",
    val forgotPasswordSuccessMessage: String = ""
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        private const val FORGOT_PASSWORD_COOLDOWN_SECONDS = 60
    }

    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state
    private var forgotPasswordCooldownJob: Job? = null

    fun login(email: String, pass: String) {
        // Validate sương sương
        if(email.isBlank() || pass.isBlank()) {
            _state.value = _state.value.copy(
                error = "Vui lòng nhập đầy đủ thông tin",
                isSuccess = false,
                successMessage = ""
            )
            return
        }

        loginUseCase(email, pass).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        isLoading = true,
                        error = "",
                        isSuccess = false,
                        successMessage = ""
                    )
                }
                is Resource.Success -> {
                    val token = result.data?.accessToken
                    val userEmail = result.data?.email ?: email

                    // Lưu token vào DataStore
                    viewModelScope.launch {
                        token?.let {
                            authRepository.saveToken(it, userEmail)
                        }
                    }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "",
                        isSuccess = true,
                        successMessage = "Xin chào, ${result.data?.name}!"
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Đăng nhập thất bại",
                        isSuccess = false,
                        successMessage = ""
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun forgotPassword(email: String) {
        val normalizedEmail = email.trim()
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

        if (_state.value.isForgotPasswordLoading) return

        if (_state.value.forgotPasswordCooldownSeconds > 0) {
            _state.value = _state.value.copy(
                forgotPasswordError = "Vui lòng chờ ${_state.value.forgotPasswordCooldownSeconds}s để gửi lại",
                forgotPasswordSuccessMessage = ""
            )
            return
        }

        if (normalizedEmail.isBlank()) {
            _state.value = _state.value.copy(
                forgotPasswordError = "Vui lòng nhập email",
                forgotPasswordSuccessMessage = ""
            )
            return
        }

        if (!normalizedEmail.matches(emailRegex)) {
            _state.value = _state.value.copy(
                forgotPasswordError = "Email không đúng định dạng",
                forgotPasswordSuccessMessage = ""
            )
            return
        }

        authRepository.forgotPassword(normalizedEmail).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        isForgotPasswordLoading = true,
                        forgotPasswordError = "",
                        forgotPasswordSuccessMessage = ""
                    )
                }

                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isForgotPasswordLoading = false,
                        forgotPasswordError = "",
                        forgotPasswordSuccessMessage = result.data
                            ?: "New password has been sent to your email"
                    )
                    startForgotPasswordCooldown()
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isForgotPasswordLoading = false,
                        forgotPasswordError = result.message ?: "Gửi yêu cầu thất bại",
                        forgotPasswordSuccessMessage = ""
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun startForgotPasswordCooldown() {
        forgotPasswordCooldownJob?.cancel()
        forgotPasswordCooldownJob = viewModelScope.launch {
            for (remaining in FORGOT_PASSWORD_COOLDOWN_SECONDS downTo 1) {
                _state.value = _state.value.copy(forgotPasswordCooldownSeconds = remaining)
                delay(1000)
            }
            _state.value = _state.value.copy(forgotPasswordCooldownSeconds = 0)
        }
    }

    fun clearForgotPasswordFeedback() {
        _state.value = _state.value.copy(
            forgotPasswordError = "",
            forgotPasswordSuccessMessage = ""
        )
    }
}
package com.example.myticketapp.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.domain.repository.AuthRepository
import com.example.myticketapp.domain.usecase.LoginUseCase
import com.example.myticketapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

// State bọc trạng thái của màn hình Login
data class LoginState(
    val isLoading: Boolean = false,
    val error: String = "",
    val isSuccess: Boolean = false,
    val successMessage: String = "" // Hoặc lưu Token ở đây
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    fun login(email: String, pass: String) {
        // Validate sương sương
        if(email.isBlank() || pass.isBlank()) {
            _state.value = LoginState(error = "Vui lòng nhập đầy đủ thông tin")
            return
        }

        loginUseCase(email, pass).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = LoginState(isLoading = true)
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

                    _state.value = LoginState(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "Xin chào, ${result.data?.name}!"
                    )
                }
                is Resource.Error -> {
                    _state.value = LoginState(
                        isLoading = false,
                        error = result.message ?: "Đăng nhập thất bại"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}
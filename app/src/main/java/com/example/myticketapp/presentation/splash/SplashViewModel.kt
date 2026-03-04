package com.example.myticketapp.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.data.usecase.CheckTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashState(
    val isLoading: Boolean = true,
    val hasToken: Boolean = false,
    val savedEmail: String? = null
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkTokenUseCase: CheckTokenUseCase
) : ViewModel() {

    private val _state = kotlinx.coroutines.flow.MutableStateFlow(SplashState())
    val state = _state

    init {
        checkToken()
    }

    private fun checkToken() {
        viewModelScope.launch {
            // Kiểm tra token
            val hasToken = checkTokenUseCase.invoke().first()
            val email = checkTokenUseCase.getSavedEmail()

            _state.value = SplashState(
                isLoading = false,
                hasToken = hasToken,
                savedEmail = email
            )
        }
    }
}

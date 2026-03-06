package com.example.myticketapp.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.data.local.TokenDataStore
import com.example.myticketapp.domain.model.UserProfile
import com.example.myticketapp.domain.repository.UserRepository
import com.example.myticketapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State cho ProfileScreen
 */
data class ProfileState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val address: String = "",
    val error: String = "",
    val showSessionExpiredDialog: Boolean = false
)

/**
 * ViewModel cho ProfileScreen
 * Quản lý dữ liệu từ API (profile) và DataStore (address)
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _state = mutableStateOf(ProfileState())
    val state: State<ProfileState> = _state

    init {
        fetchProfile()
        listenToAddress()
    }

    /**
     * Lấy thông tin profile từ API
     */
    private fun fetchProfile() {
        repository.getMyProfile().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        profile = result.data
                    )
                }
                is Resource.Error -> {
                    if (result.message == "TOKEN_EXPIRED") {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            showSessionExpiredDialog = true
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = result.message ?: "Lỗi tải thông tin"
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Lắng nghe thay đổi địa chỉ từ DataStore
     */
    private fun listenToAddress() {
        tokenDataStore.userAddress.onEach { addr ->
            _state.value = _state.value.copy(address = addr)
        }.launchIn(viewModelScope)
    }

    /**
     * Lưu địa chỉ vào DataStore
     */
    fun saveAddress(newAddress: String) {
        viewModelScope.launch {
            tokenDataStore.saveAddress(newAddress)
        }
    }

    /**
     * Xử lý khi user xác nhận session expired
     */
    fun onSessionExpiredConfirmed() {
        _state.value = _state.value.copy(showSessionExpiredDialog = false)
    }

    /**
     * Đăng xuất - xóa session và gọi callback
     */
    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            tokenDataStore.clearSession()
            onLogoutSuccess()
        }
    }
}

package com.example.myticketapp.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.data.local.TokenDataStore
import com.example.myticketapp.domain.model.ChangePasswordRequest
import com.example.myticketapp.domain.model.UpdateUserProfileRequest
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
    val isSaving: Boolean = false,
    val isChangingPassword: Boolean = false,
    val profile: UserProfile? = null,
    val address: String = "",
    val error: String = "",
    val passwordError: String = "",
    val showSessionExpiredDialog: Boolean = false,
    val showUpdateSuccessDialog: Boolean = false,
    val showChangePasswordSuccessDialog: Boolean = false
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
                    _state.value = _state.value.copy(
                        isLoading = true,
                        error = ""
                    )
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "",
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
     * Cập nhật thông tin profile lên server
     */
    fun updateProfile(
        name: String,
        email: String,
        phone: String,
        address: String,
        birthday: String
    ) {
        if (
            name.isBlank() ||
            email.isBlank() ||
            phone.isBlank() ||
            address.isBlank() ||
            birthday.isBlank()
        ) {
            _state.value = _state.value.copy(error = "Vui lòng nhập đầy đủ thông tin")
            return
        }

        val birthdayRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        if (!birthday.matches(birthdayRegex)) {
            _state.value = _state.value.copy(error = "Ngày sinh phải đúng định dạng YYYY-MM-DD")
            return
        }

        repository.updateMyProfile(
            UpdateUserProfileRequest(
                name = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                address = address.trim(),
                birthday = birthday.trim()
            )
        ).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        isSaving = true,
                        error = "",
                        passwordError = ""
                    )
                }

                is Resource.Success -> {
                    val responseProfile = result.data ?: return@onEach
                    val updatedProfile = responseProfile.copy(
                        name = responseProfile.name.ifBlank { name.trim() },
                        email = responseProfile.email.ifBlank { email.trim() },
                        phone = responseProfile.phone.ifBlank { phone.trim() },
                        address = responseProfile.address.ifBlank { address.trim() },
                        birthday = responseProfile.birthday.ifBlank { birthday.trim() }
                    )

                    viewModelScope.launch {
                        tokenDataStore.saveUserProfile(
                            name = updatedProfile.name,
                            email = updatedProfile.email,
                            phone = updatedProfile.phone,
                            address = updatedProfile.address,
                            birthday = updatedProfile.birthday
                        )
                    }

                    _state.value = _state.value.copy(
                        isSaving = false,
                        error = "",
                        passwordError = "",
                        profile = updatedProfile,
                        address = updatedProfile.address,
                        showUpdateSuccessDialog = true
                    )
                }

                is Resource.Error -> {
                    if (result.message == "TOKEN_EXPIRED") {
                        _state.value = _state.value.copy(
                            isSaving = false,
                            showSessionExpiredDialog = true
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isSaving = false,
                            error = result.message ?: "Cập nhật thông tin thất bại"
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun dismissUpdateSuccessDialog() {
        _state.value = _state.value.copy(showUpdateSuccessDialog = false)
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        val oldPwd = oldPassword.trim()
        val newPwd = newPassword.trim()
        val confirmPwd = confirmPassword.trim()

        if (oldPwd.isBlank() || newPwd.isBlank() || confirmPwd.isBlank()) {
            _state.value = _state.value.copy(passwordError = "Vui lòng nhập đầy đủ thông tin mật khẩu")
            return
        }

        if (newPwd.length < 6) {
            _state.value = _state.value.copy(passwordError = "Mật khẩu mới phải có ít nhất 6 ký tự")
            return
        }

        if (oldPwd == newPwd) {
            _state.value = _state.value.copy(passwordError = "Mật khẩu mới phải khác mật khẩu cũ")
            return
        }

        if (newPwd != confirmPwd) {
            _state.value = _state.value.copy(passwordError = "Xác nhận mật khẩu không khớp")
            return
        }

        repository.changePassword(
            ChangePasswordRequest(
                oldPassword = oldPwd,
                newPassword = newPwd,
                confirmPassword = confirmPwd
            )
        ).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        isChangingPassword = true,
                        passwordError = ""
                    )
                }

                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isChangingPassword = false,
                        passwordError = "",
                        showChangePasswordSuccessDialog = true
                    )
                }

                is Resource.Error -> {
                    if (result.message == "TOKEN_EXPIRED") {
                        _state.value = _state.value.copy(
                            isChangingPassword = false,
                            showSessionExpiredDialog = true
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isChangingPassword = false,
                            passwordError = result.message ?: "Đổi mật khẩu thất bại"
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun dismissChangePasswordSuccessDialog() {
        _state.value = _state.value.copy(showChangePasswordSuccessDialog = false)
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

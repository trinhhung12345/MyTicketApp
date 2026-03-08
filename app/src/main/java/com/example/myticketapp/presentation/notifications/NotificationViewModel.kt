package com.example.myticketapp.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.domain.model.Notification
import com.example.myticketapp.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel quản lý danh sách thông báo
 * Lắng nghe liên tục từ Room Database
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    /**
     * Lắng nghe liên tục từ Database (Single Source of Truth)
     * Database thay đổi → Flow thay đổi → UI tự động cập nhật
     */
    val notifications: StateFlow<List<Notification>> = repository.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Đánh dấu một thông báo đã đọc
     */
    fun markAsRead(id: String) {
        viewModelScope.launch {
            repository.markAsRead(id)
        }
    }

    /**
     * Đánh dấu tất cả thông báo đã đọc
     */
    fun markAllAsRead() {
        viewModelScope.launch {
            repository.markAllAsRead()
        }
    }

    /**
     * Xóa tất cả thông báo
     */
    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}

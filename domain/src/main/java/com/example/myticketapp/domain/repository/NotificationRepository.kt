package com.example.myticketapp.domain.repository

import com.example.myticketapp.domain.model.Notification
import kotlinx.coroutines.flow.Flow

/**
 * Repository quản lý thông báo (CRUD)
 */
interface NotificationRepository {
    fun getAllNotifications(): Flow<List<Notification>>
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    suspend fun clearAll()
}

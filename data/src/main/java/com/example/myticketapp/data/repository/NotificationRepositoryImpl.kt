package com.example.myticketapp.data.repository

import com.example.myticketapp.data.local.room.NotificationDao
import com.example.myticketapp.data.local.room.toDomain
import com.example.myticketapp.domain.model.Notification
import com.example.myticketapp.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val dao: NotificationDao
) : NotificationRepository {

    override fun getAllNotifications(): Flow<List<Notification>> {
        return dao.getAllNotifications().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun markAsRead(id: String) {
        dao.markAsRead(id)
    }

    override suspend fun markAllAsRead() {
        dao.markAllAsRead()
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }
}

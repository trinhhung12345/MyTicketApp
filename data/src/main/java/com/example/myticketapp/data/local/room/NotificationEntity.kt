package com.example.myticketapp.data.local.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Entity đại diện cho một thông báo trong Room Database
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val title: String,
    val message: String,
    val createdAt: String,
    val isRead: Boolean = false
)

/**
 * DAO cho các thao tác với thông báo
 */
@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}

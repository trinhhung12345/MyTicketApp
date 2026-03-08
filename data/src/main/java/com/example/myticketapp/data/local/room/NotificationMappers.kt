package com.example.myticketapp.data.local.room

import com.example.myticketapp.domain.model.Notification

fun NotificationEntity.toDomain(): Notification {
    return Notification(
        id = this.id,
        eventId = this.eventId,
        title = this.title,
        message = this.message,
        createdAt = this.createdAt,
        isRead = this.isRead
    )
}

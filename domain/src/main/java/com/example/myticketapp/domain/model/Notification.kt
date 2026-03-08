package com.example.myticketapp.domain.model

/**
 * Domain model đại diện cho một thông báo
 */
data class Notification(
    val id: String,
    val eventId: String,
    val title: String,
    val message: String,
    val createdAt: String,
    val isRead: Boolean
)

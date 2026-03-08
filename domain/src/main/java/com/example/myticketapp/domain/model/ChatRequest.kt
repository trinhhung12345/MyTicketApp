package com.example.myticketapp.domain.model

/**
 * Request gửi lên API Chatbot
 * Có thể gửi message thuần hoặc kèm recipient info (khi điền form)
 */
data class ChatRequest(
    val message: String,
    val recipient: Recipient? = null
)

/**
 * Model chứa thông tin người nhận (dùng khi đặt vé)
 */
data class Recipient(
    val name: String,
    val phone: String,
    val email: String,
    val address: String
)

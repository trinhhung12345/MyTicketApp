package com.example.myticketapp.domain.model

import java.util.UUID

/**
 * Model đại diện cho một tin nhắn trong chat
 * Dùng để hiển thị lên giao diện
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val isFromUser: Boolean,
    val text: String,
    val intent: String? = null,
    val options: List<ChatOption>? = null,
    val formFields: List<FormField>? = null
)

/**
 * Model cho option (nút bấm nhanh)
 */
data class ChatOption(
    val label: String
)

/**
 * Model cho trường form cần nhập
 */
data class FormField(
    val label: String,
    val name: String,
    val required: Boolean
)

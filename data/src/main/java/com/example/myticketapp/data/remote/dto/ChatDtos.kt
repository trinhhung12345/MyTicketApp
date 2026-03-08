package com.example.myticketapp.data.remote.dto

/**
 * DTO Request gửi lên API Chatbot
 * Có thể gửi message thuần hoặc kèm recipient info (khi điền form)
 */
data class ChatRequestDto(
    val message: String,
    val recipient: RecipientDto? = null
)

/**
 * DTO chứa thông tin người nhận (dùng khi đặt vé)
 */
data class RecipientDto(
    val name: String,
    val phone: String,
    val email: String,
    val address: String
)

/**
 * Response trả về từ API Chatbot
 * API này trả về trực tiếp object thay vì bọc trong BaseResponse
 */
data class ChatResponseDto(
    val reply: String,
    val intent: String?,
    val options: List<ChatOptionDto>?,
    val formFields: List<FormFieldDto>?
)

/**
 * DTO cho các option (nút bấm nhanh)
 */
data class ChatOptionDto(
    val value: Any,
    val label: String
)

/**
 * DTO cho các trường form cần nhập
 */
data class FormFieldDto(
    val label: String,
    val name: String,
    val required: Boolean
)

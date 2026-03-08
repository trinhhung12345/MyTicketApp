package com.example.myticketapp.data.remote.api

import com.example.myticketapp.data.remote.dto.ChatRequestDto
import com.example.myticketapp.data.remote.dto.ChatResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API interface cho Chatbot
 */
interface ChatApi {
    /**
     * Gửi tin nhắn đến chatbot và nhận phản hồi
     * Endpoint: POST /chatbot/message
     */
    @POST("chatbot/message")
    suspend fun sendMessage(@Body request: ChatRequestDto): ChatResponseDto
}

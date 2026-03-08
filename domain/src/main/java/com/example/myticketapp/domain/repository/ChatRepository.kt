package com.example.myticketapp.domain.repository

import com.example.myticketapp.domain.model.ChatMessage
import com.example.myticketapp.domain.model.ChatRequest
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface cho Chatbot
 */
interface ChatRepository {
    /**
     * Gửi tin nhắn đến chatbot và nhận phản hồi
     */
    fun sendMessage(request: ChatRequest): Flow<Resource<ChatMessage>>
}

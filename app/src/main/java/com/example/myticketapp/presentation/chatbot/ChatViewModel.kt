package com.example.myticketapp.presentation.chatbot

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myticketapp.data.local.TokenDataStore
import com.example.myticketapp.domain.model.ChatMessage
import com.example.myticketapp.domain.model.ChatRequest
import com.example.myticketapp.domain.model.Recipient
import com.example.myticketapp.domain.repository.ChatRepository
import com.example.myticketapp.domain.repository.UserRepository
import com.example.myticketapp.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val userRepository: UserRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    // Lịch sử tin nhắn
    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    // Trạng thái đang gõ (Typing)
    private val _isTyping = mutableStateOf(false)
    val isTyping: State<Boolean> = _isTyping

    // Thông tin người dùng để Autofill Form
    var autofillName = ""
        private set
    var autofillPhone = ""
        private set
    var autofillEmail = ""
        private set
    var autofillAddress = ""
        private set

    init {
        // Load data autofill trước
        viewModelScope.launch {
            autofillAddress = tokenDataStore.userAddress.first()
            
            userRepository.getMyProfile().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { profile ->
                            autofillName = profile.name
                            autofillPhone = profile.phone
                            autofillEmail = profile.email
                        }
                    }
                    is Resource.Error -> {
                        // Ignore error, use empty values
                    }
                    is Resource.Loading -> {
                        // Do nothing
                    }
                }
            }
        }
        
        // Gửi lời chào mặc định
        _messages.add(
            ChatMessage(
                isFromUser = false,
                text = "Xin chào! Tôi là TixCon Bot. Tôi có thể giúp gì cho bạn? (Ví dụ: Tìm vé Tokyo Girls)"
            )
        )
    }

    // 1. Gửi Text bình thường hoặc Chọn Option
    fun sendTextMessage(text: String) {
        if (text.isBlank()) return
        
        // Thêm tin nhắn của User vào UI
        _messages.add(ChatMessage(isFromUser = true, text = text))
        
        callChatApi(ChatRequest(message = text))
    }

    // 2. Gửi Form
    fun sendFormMessage(name: String, phone: String, email: String, address: String) {
        // Hiển thị tin nhắn giả định cho User
        _messages.add(ChatMessage(isFromUser = true, text = "Đã gửi thông tin cá nhân"))
        
        val recipient = Recipient(name, phone, email, address)
        callChatApi(ChatRequest(message = "", recipient = recipient))
    }

    private fun callChatApi(request: ChatRequest) {
        _isTyping.value = true
        viewModelScope.launch {
            repository.sendMessage(request).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _isTyping.value = false
                        result.data?.let { _messages.add(it) }
                    }
                    is Resource.Error -> {
                        _isTyping.value = false
                        _messages.add(
                            ChatMessage(
                                isFromUser = false,
                                text = "Xin lỗi, đã có lỗi xảy ra. Vui lòng thử lại."
                            )
                        )
                    }
                    is Resource.Loading -> { /* Do nothing */ }
                }
            }
        }
    }
}

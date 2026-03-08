package com.example.myticketapp.data.repository

import android.util.Log
import com.example.myticketapp.data.remote.api.ChatApi
import com.example.myticketapp.data.remote.dto.ChatRequestDto
import com.example.myticketapp.data.remote.dto.RecipientDto
import com.example.myticketapp.domain.model.ChatMessage
import com.example.myticketapp.domain.model.ChatOption
import com.example.myticketapp.domain.model.ChatRequest
import com.example.myticketapp.domain.model.FormField
import com.example.myticketapp.domain.model.Recipient
import com.example.myticketapp.domain.repository.ChatRepository
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val TAG = "ChatRepositoryImpl"

class ChatRepositoryImpl @Inject constructor(
    private val api: ChatApi
) : ChatRepository {

    override fun sendMessage(request: ChatRequest): Flow<Resource<ChatMessage>> = flow {
        emit(Resource.Loading())
        try {
            Log.d(TAG, "sendMessage: message=${request.message}, recipient=${request.recipient}")
            
            // Chuyển từ domain request sang DTO request
            val dtoRequest = ChatRequestDto(
                message = request.message,
                recipient = request.recipient?.let { 
                    RecipientDto(it.name, it.phone, it.email, it.address) 
                }
            )
            
            val response = api.sendMessage(dtoRequest)
            
            Log.d(TAG, "response: reply=${response.reply}, intent=${response.intent}")
            
            val botMessage = ChatMessage(
                isFromUser = false,
                text = response.reply,
                intent = response.intent,
                options = response.options?.map { ChatOption(it.label) },
                formFields = response.formFields?.map { 
                    FormField(it.label, it.name, it.required) 
                }
            )
            emit(Resource.Success(botMessage))
        } catch (e: HttpException) {
            Log.e(TAG, "HttpException: code=${e.code()}, message=${e.message}")
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        } catch (e: IOException) {
            Log.e(TAG, "IOException: ${e.message}")
            emit(Resource.Error("Không thể kết nối Internet"))
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
            emit(Resource.Error("Lỗi: ${e.message}"))
        }
    }
}

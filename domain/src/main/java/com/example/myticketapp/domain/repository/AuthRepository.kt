package com.example.myticketapp.domain.repository

import com.example.myticketapp.domain.model.RegisterRequest
import com.example.myticketapp.domain.model.User
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<User>>
    fun sendOtp(phone: String, email: String): Flow<Resource<String>>
    fun register(request: RegisterRequest): Flow<Resource<User>>
    suspend fun saveToken(token: String, email: String)
    suspend fun clearToken()
}
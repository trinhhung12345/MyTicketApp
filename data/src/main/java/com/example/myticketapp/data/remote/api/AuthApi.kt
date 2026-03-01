package com.example.myticketapp.data.remote.api

import com.example.myticketapp.data.remote.dto.BaseResponse
import com.example.myticketapp.data.remote.dto.LoginRequest
import com.example.myticketapp.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login") // Kết hợp với API_BASE_URL ở module app
    suspend fun login(@Body request: LoginRequest): BaseResponse<UserDto>
}
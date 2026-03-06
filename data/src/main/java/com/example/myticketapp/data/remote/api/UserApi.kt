package com.example.myticketapp.data.remote.api

import com.example.myticketapp.data.remote.dto.BaseResponse
import com.example.myticketapp.data.remote.dto.UserProfileDto
import retrofit2.http.GET

/**
 * API interface cho các endpoint liên quan đến User
 */
interface UserApi {
    /**
     * Lấy thông tin profile của user hiện tại
     * Endpoint: GET /users/me
     */
    @GET("users/me")
    suspend fun getMyProfile(): BaseResponse<UserProfileDto>
}

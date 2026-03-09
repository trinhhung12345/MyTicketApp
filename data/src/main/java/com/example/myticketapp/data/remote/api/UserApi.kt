package com.example.myticketapp.data.remote.api

import com.example.myticketapp.data.remote.dto.BaseResponse
import com.example.myticketapp.data.remote.dto.ChangePasswordRequestDto
import com.example.myticketapp.data.remote.dto.UpdateUserProfileRequestDto
import com.example.myticketapp.data.remote.dto.UserProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

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

    /**
     * Cập nhật thông tin profile của user hiện tại
     * Endpoint: PUT /users/me
     */
    @PUT("users/me")
    suspend fun updateMyProfile(
        @Body request: UpdateUserProfileRequestDto
    ): BaseResponse<UserProfileDto>

    /**
     * Đổi mật khẩu user hiện tại
     * Endpoint: PUT /user/me/password
     */
    @PUT("users/me/password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequestDto
    ): BaseResponse<Any>
}

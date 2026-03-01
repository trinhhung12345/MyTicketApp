package com.example.myticketapp.data.remote.dto

import com.example.myticketapp.domain.model.User
import com.google.gson.annotations.SerializedName

// Request Body
data class LoginRequest(
    val email: String,
    val password: String
)

// Base Response bọc data bên ngoài
data class BaseResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)

// DTO của User trả về
data class UserDto(
    val id: Int,
    val code: String,
    val phone: String,
    val name: String,
    val email: String,
    val role: RoleDto,
    val accessToken: String
)

data class RoleDto(
    val roleId: Int,
    val roleName: String
)

// Hàm mở rộng (Extension function) để chuyển từ DTO của Data sang Model của Domain
fun UserDto.toDomainModel(): User {
    return User(
        id = this.id,
        name = this.name,
        email = this.email,
        phone = this.phone,
        roleName = this.role.roleName,
        accessToken = this.accessToken
    )
}
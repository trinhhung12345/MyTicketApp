package com.example.myticketapp.data.remote.dto

import com.example.myticketapp.domain.model.UserProfile

/**
 * DTO cho thông tin User Profile trả về từ API
 */
data class UserProfileDto(
    val id: Int,
    val code: String,
    val phone: String,
    val name: String,
    val email: String,
    val role: RoleDto
)

/**
 * Extension function chuyển từ DTO sang Domain Model
 */
fun UserProfileDto.toDomain(): UserProfile {
    return UserProfile(
        id = this.id,
        code = this.code,
        name = this.name,
        email = this.email,
        phone = this.phone,
        roleName = this.role.roleName
    )
}

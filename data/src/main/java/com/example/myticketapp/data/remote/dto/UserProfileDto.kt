package com.example.myticketapp.data.remote.dto

import com.example.myticketapp.domain.model.UserProfile

/**
 * DTO cho thông tin User Profile trả về từ API
 */
data class UserProfileDto(
    val id: Int,
    val code: String? = null,
    val phone: String? = null,
    val name: String? = null,
    val email: String? = null,
    val address: String? = null,
    val birthday: String? = null,
    val status: Int? = null,
    val role: RoleDto? = null
)

/**
 * DTO cho body cập nhật profile
 */
data class UpdateUserProfileRequestDto(
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val birthday: String
)

data class ChangePasswordRequestDto(
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

/**
 * Extension function chuyển từ DTO sang Domain Model
 */
fun UserProfileDto.toDomain(): UserProfile {
    return UserProfile(
        id = this.id,
        code = this.code.orEmpty(),
        name = this.name.orEmpty(),
        email = this.email.orEmpty(),
        phone = this.phone.orEmpty(),
        roleName = this.role?.roleName ?: "USER",
        address = this.address.orEmpty(),
        birthday = this.birthday
            .orEmpty()
            .substringBefore(" ")
            .substringBefore("T"),
        status = this.status ?: 1
    )
}

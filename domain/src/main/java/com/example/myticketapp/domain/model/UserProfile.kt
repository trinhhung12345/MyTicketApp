package com.example.myticketapp.domain.model

/**
 * Model đại diện cho thông tin cá nhân của user
 */
data class UserProfile(
    val id: Int,
    val code: String,
    val name: String,
    val email: String,
    val phone: String,
    val roleName: String
)

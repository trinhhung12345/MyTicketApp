package com.example.myticketapp.domain.model

data class RegisterRequest(
    val accountPhone: String,
    val email: String,
    val accountName: String,
    val password: String,
    val confirmPassword: String,
    val code: String, // Mã OTP
    val type: String = "1",
    val businessRole: Int = 1,
    val purpose: String = "1"
)

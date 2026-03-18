package com.example.myticketapp.domain.model

data class UpdateUserProfileRequest(
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val birthday: String
)

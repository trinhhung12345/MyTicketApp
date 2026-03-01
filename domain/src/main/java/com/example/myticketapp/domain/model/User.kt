package com.example.myticketapp.domain.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val roleName: String,
    val accessToken: String
)
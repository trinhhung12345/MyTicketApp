package com.example.myticketapp.domain.model

data class Event(
    val id: Int,
    val title: String,
    val venue: String,
    val categoryName: String,
    val thumbnailUrl: String, // Đã lọc file type = 0
    val minPrice: Long,       // Đã tìm giá nhỏ nhất
    val startDate: String     // Đã format ngày sớm nhất
)
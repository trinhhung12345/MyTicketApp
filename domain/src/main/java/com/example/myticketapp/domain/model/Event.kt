package com.example.myticketapp.domain.model

data class Event(
    val id: Int,
    val title: String,
    val venue: String,
    val address: String, // Thêm địa chỉ
    val descriptionHtml: String, // Thêm mô tả
    val categoryName: String,
    val thumbnailUrl: String,
    val bannerUrl: String, // Ảnh bìa (Không phải thumbnail)
    val youtubeUrl: String?, // Link Youtube nếu có
    val gallery: List<String>, // Danh sách toàn bộ ảnh
    val minPrice: Long,
    val startDate: String,
    val startTime: String, // Giờ bắt đầu
    val showings: List<Showing> // Danh sách suất diễn
)

data class Showing(
    val id: Int,
    val startTime: String,
    val isSalable: Boolean,
    val minPrice: Long,
    val types: List<TicketType>
)

data class TicketType(
    val id: Int,
    val name: String,
    val description: String,
    val color: String,
    val isFree: Boolean,
    val price: Long,
    val originalPrice: Long,
    val maxQtyPerOrder: Int,
    val minQtyPerOrder: Int,
    val quantity: Int,
    val remainingQuantity: Int,
    val startTime: String,
    val endTime: String,
    val position: Int,
    val status: String,
    val imageUrl: String,
    val showingId: Int?
)
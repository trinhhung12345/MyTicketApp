package com.example.myticketapp.data.remote.dto

import com.example.myticketapp.domain.model.Category
import com.example.myticketapp.domain.model.Event
import java.text.SimpleDateFormat
import java.util.Locale

// --- Category DTO ---
data class CategoryDto(val id: Int, val name: String, val active: Boolean)

fun CategoryDto.toDomain() = Category(id = id, name = name)

// --- Event DTO ---
data class EventDto(
    val id: Int,
    val title: String,
    val venue: String,
    val categoryName: String,
    val files: List<FileDto>?,
    val showings: List<ShowingDto>?
)

data class FileDto(val type: Int, val originUrl: String)
data class ShowingDto(val startTime: String, val types: List<TicketTypeDto>?)
data class TicketTypeDto(val price: Long)

// --- MAPPER LOGIC QUAN TRỌNG ---
fun EventDto.toDomain(): Event {
    // 1. Tìm Thumbnail: Lấy file đầu tiên có type == 0 (Image)
    val thumbnail = files?.firstOrNull { it.type == 0 }?.originUrl ?: ""

    // 2. Tìm giá Min: Duyệt qua tất cả showings -> types -> lấy price nhỏ nhất
    val minPrice = showings?.flatMap { it.types ?: emptyList() }
        ?.minOfOrNull { it.price } ?: 0L

    // 3. Tìm ngày diễn ra (Lấy startTime của showing đầu tiên) và Format
    var formattedDate = "Đang cập nhật"
    val rawDate = showings?.firstOrNull()?.startTime
    if (!rawDate.isNullOrEmpty()) {
        try {
            // Chuỗi từ API: "2026-03-01T00:17:00"
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = parser.parse(rawDate)
            if (date != null) formattedDate = formatter.format(date)
        } catch (e: Exception) {
            formattedDate = rawDate.take(10) // Fallback lấy "2026-03-01"
        }
    }

    return Event(
        id = id,
        title = title,
        venue = venue,
        categoryName = categoryName,
        thumbnailUrl = thumbnail,
        minPrice = minPrice,
        startDate = formattedDate
    )
}
package com.example.myticketapp.data.remote.dto

import com.example.myticketapp.domain.model.Category
import com.example.myticketapp.domain.model.Event
import com.example.myticketapp.domain.model.Showing
import com.example.myticketapp.domain.model.TicketType
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
    val showings: List<ShowingDto>?,
    val address: String?,
    val description: String?
)

data class FileDto(val type: Int, val originUrl: String)
data class ShowingDto(val id: Int, val startTime: String, val isSalable: Boolean, val types: List<TicketTypeDto>?)
data class TicketTypeDto(
    val id: Int?,
    val name: String?,
    val description: String?,
    val color: String?,
    val isFree: Boolean?,
    val price: Long?,
    val originalPrice: Long?,
    val maxQtyPerOrder: Int?,
    val minQtyPerOrder: Int?,
    val quantity: Int?,
    val remainingQuantity: Int?,
    val startTime: String?,
    val endTime: String?,
    val position: Int?,
    val status: String?,
    val imageUrl: String?,
    val showingId: Int?
)

// --- MAPPER LOGIC QUAN TRỌNG ---
fun EventDto.toDomain(): Event {
    // 1. Phân loại Files
    val imageFiles = files?.filter { it.type == 0 }?.map { it.originUrl } ?: emptyList()
    val videoFile = files?.firstOrNull { it.type == 1 }?.originUrl

    val thumbnailUrl = imageFiles.firstOrNull() ?: ""
    // Banner: Lấy ảnh thứ 2, nếu không có thì lấy ảnh 1
    val bannerUrl = imageFiles.getOrNull(1) ?: thumbnailUrl

    // 2. Logic tính giá Min cho toàn bộ Event
    val minPrice = showings?.flatMap { it.types ?: emptyList() }
        ?.minOfOrNull { it.price ?: 0L } ?: 0L

    // 3. Xử lý Thời gian suất diễn đầu tiên
    var formattedDate = "Đang cập nhật"
    var formattedTime = "--:--"
    val firstShowing = showings?.firstOrNull()
    if (firstShowing?.startTime != null) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = parser.parse(firstShowing.startTime)
            if (date != null) {
                formattedDate = SimpleDateFormat("EE, dd/MM/yyyy", Locale("vi", "VN")).format(date)
                formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            }
        } catch (e: Exception) { /* Fallback */ }
    }

    // 4. Map danh sách Showings
    val mappedShowings = showings?.map { showing ->
        val mappedTypes = showing.types?.map { type ->
            TicketType(
                id = type.id ?: 0,
                name = type.name.orEmpty(),
                description = type.description.orEmpty(),
                color = type.color.orEmpty(),
                isFree = type.isFree ?: false,
                price = type.price ?: 0L,
                originalPrice = type.originalPrice ?: 0L,
                maxQtyPerOrder = type.maxQtyPerOrder ?: 0,
                minQtyPerOrder = type.minQtyPerOrder ?: 0,
                quantity = type.quantity ?: 0,
                remainingQuantity = type.remainingQuantity ?: 0,
                startTime = type.startTime.orEmpty(),
                endTime = type.endTime.orEmpty(),
                position = type.position ?: 0,
                status = type.status.orEmpty(),
                imageUrl = type.imageUrl.orEmpty(),
                showingId = type.showingId
            )
        } ?: emptyList()

        val showingMinPrice = mappedTypes.minOfOrNull { it.price } ?: 0L
        Showing(
            id = showing.id,
            startTime = showing.startTime,
            isSalable = showing.isSalable,
            minPrice = showingMinPrice,
            types = mappedTypes
        )
    } ?: emptyList()

    return Event(
        id = id,
        title = title,
        venue = venue,
        address = address ?: venue,
        descriptionHtml = description ?: "",
        categoryName = categoryName,
        thumbnailUrl = thumbnailUrl,
        bannerUrl = bannerUrl,
        youtubeUrl = videoFile,
        gallery = imageFiles,
        minPrice = minPrice,
        startDate = formattedDate,
        startTime = formattedTime,
        showings = mappedShowings
    )
}
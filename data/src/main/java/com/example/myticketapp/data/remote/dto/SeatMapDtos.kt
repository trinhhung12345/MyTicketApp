package com.example.myticketapp.data.remote.dto

import com.example.myticketapp.domain.model.*

data class SeatMapResponse(
    val id: Int,
    val name: String,
    val viewbox: String,
    val sections: List<SectionDto>
)

data class SectionDto(
    val id: Int,
    val name: String,
    val isStage: Boolean,
    val ticketTypeId: Int?,
    val attribute: AttributeDto,
    val seats: List<SeatDto>
)

data class AttributeDto(
    val x: Float, val y: Float, val width: Float, val height: Float, val fill: String
)

data class SeatDto(
    val id: Int, val code: String, val rowIndex: Int, val colIndex: Int, val status: String
)

// --- MAPPER ---
fun SeatMapResponse.toDomain(ticketTypes: List<TicketTypeDto>): SeatMap {
    val dimensions = viewbox.split(" ")
    val width = dimensions.getOrNull(2)?.toFloatOrNull() ?: 1200f
    val height = dimensions.getOrNull(3)?.toFloatOrNull() ?: 800f

    return SeatMap(
        id = id,
        viewboxWidth = width,
        viewboxHeight = height,
        sections = sections.map { sec ->
            // Tìm TicketType khớp với ticketTypeId của Section này
            val matchedTicketType = ticketTypes.find { it.id == sec.ticketTypeId }

            // Lấy giá và giới hạn, nếu không có thì mặc định 0 và 4
            val sectionPrice = matchedTicketType?.price ?: 0L
            val maxQty = matchedTicketType?.maxQtyPerOrder ?: 4

            Section(
                id = sec.id,
                name = sec.name,
                isStage = sec.isStage,
                ticketTypeId = sec.ticketTypeId,
                maxQtyPerOrder = maxQty, // Gắn giới hạn mua
                attribute = SectionAttribute(
                    x = sec.attribute.x, y = sec.attribute.y,
                    width = sec.attribute.width, height = sec.attribute.height,
                    color = android.graphics.Color.parseColor(sec.attribute.fill)
                ),
                seats = sec.seats.map { seat ->
                    Seat(
                        id = seat.id, code = seat.code,
                        rowIndex = seat.rowIndex, colIndex = seat.colIndex,
                        status = seat.status,
                        isSalable = true, // Giả định
                        price = sectionPrice // ĐÃ GÁN GIÁ CHUẨN XÁC TỪ TICKET TYPE
                    )
                }
            )
        }
    )
}

fun TicketTypeDto.toDomain() = com.example.myticketapp.domain.model.TicketType(
    id = id ?: 0,
    name = name ?: "",
    description = description ?: name ?: "",
    color = color ?: "",
    isFree = isFree ?: false,
    price = price ?: 0L,
    originalPrice = originalPrice ?: 0L,
    maxQtyPerOrder = maxQtyPerOrder ?: 0,
    minQtyPerOrder = minQtyPerOrder ?: 0,
    quantity = quantity ?: 0,
    remainingQuantity = remainingQuantity ?: 0,
    startTime = startTime ?: "",
    endTime = endTime ?: "",
    position = position ?: 0,
    status = status ?: "",
    imageUrl = imageUrl ?: "",
    showingId = showingId
)

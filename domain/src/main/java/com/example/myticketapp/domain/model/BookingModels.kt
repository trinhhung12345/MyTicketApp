package com.example.myticketapp.domain.model

// Khối dữ liệu gộp trả về cho ViewModel
data class BookingInitData(
    val seatMap: SeatMap?, // Có thể null nếu mảng rỗng
    val ticketTypes: List<TicketType>
)

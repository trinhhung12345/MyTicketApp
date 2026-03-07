package com.example.myticketapp.domain.model

data class CheckoutCart(
    val eventName: String,
    val showingId: Int,
    val showingTime: String = "",
    val hasSeatMap: Boolean,
    val selectedSeats: List<CheckoutSeatItem> = emptyList(),
    val selectedTickets: List<TicketOrderItem> = emptyList()
)

data class CheckoutSeatItem(
    val id: Int,
    val code: String,
    val price: Long,
    val sectionName: String = ""
)

data class TicketOrderItem(
    val ticketTypeId: Int,
    val ticketTypeName: String,
    val price: Long,
    val quantity: Int
)
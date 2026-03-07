package com.example.myticketapp.domain.model

data class Order(
    val id: Int,
    val code: String,
    val totalAmount: Long,
    val totalQuantity: Int,
    val status: String,
    val createdAt: String,
    val paymentAt: String? = null,
    val recipientName: String = "",
    val recipientPhone: String = "",
    val recipientEmail: String = "",
    val recipientAddress: String = "",
    val orderDetails: List<OrderDetail> = emptyList()
)

data class OrderDetail(
    val id: Int,
    val seatId: Int,
    val seatCode: String,
    val price: Long,
    val originalPrice: Long? = null,
    val qr: String? = null
)

data class CreateOrderPayload(
    val recipientName: String,
    val recipientPhone: String,
    val recipientEmail: String,
    val recipientAddress: String,
    val seatIds: List<Int>? = null,
    val ticketTypeId: Int? = null,
    val quantity: Int? = null
)
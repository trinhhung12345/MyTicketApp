package com.example.myticketapp.data.remote.dto

import com.example.myticketapp.domain.model.CreateOrderPayload
import com.example.myticketapp.domain.model.OrderDetail
import com.example.myticketapp.domain.model.Order

data class CreateOrderRequest(
    val recipientName: String,
    val recipientPhone: String,
    val recipientEmail: String,
    val recipientAddress: String,
    val seatIds: List<Int>? = null,
    val ticketTypeId: Int? = null,
    val quantity: Int? = null
)

data class CheckoutRequest(
    val orderId: Int
)

data class OrderDto(
    val id: Int? = null,
    val code: String? = null,
    val totalAmount: Long? = null,
    val totalQuantity: Int? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val paymentAt: String? = null,
    val recipientName: String? = null,
    val recipientPhone: String? = null,
    val recipientEmail: String? = null,
    val recipientAddress: String? = null,
    val orderDetails: List<OrderDetailDto>? = null
)

data class OrderDetailDto(
    val id: Int? = null,
    val seatId: Int? = null,
    val seatCode: String? = null,
    val price: Long? = null,
    val originalPrice: Long? = null,
    val qr: String? = null
)

fun CreateOrderPayload.toDto() = CreateOrderRequest(
    recipientName = recipientName,
    recipientPhone = recipientPhone,
    recipientEmail = recipientEmail,
    recipientAddress = recipientAddress,
    seatIds = seatIds,
    ticketTypeId = ticketTypeId,
    quantity = quantity
)

fun OrderDto.toDomain() = Order(
    id = id ?: 0,
    code = code.orEmpty(),
    totalAmount = totalAmount ?: 0L,
    totalQuantity = totalQuantity ?: 0,
    status = status.orEmpty(),
    createdAt = createdAt.orEmpty(),
    paymentAt = paymentAt,
    recipientName = recipientName.orEmpty(),
    recipientPhone = recipientPhone.orEmpty(),
    recipientEmail = recipientEmail.orEmpty(),
    recipientAddress = recipientAddress.orEmpty(),
    orderDetails = orderDetails.orEmpty().map { it.toDomain() }
)

fun OrderDetailDto.toDomain() = OrderDetail(
    id = id ?: 0,
    seatId = seatId ?: 0,
    seatCode = seatCode.orEmpty(),
    price = price ?: 0L,
    originalPrice = originalPrice,
    qr = qr
)
package com.example.myticketapp.data.remote.api

import com.example.myticketapp.data.remote.dto.BaseResponse
import com.example.myticketapp.data.remote.dto.CheckoutRequest
import com.example.myticketapp.data.remote.dto.CreateOrderRequest
import com.example.myticketapp.data.remote.dto.OrderDto
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.POST

interface OrderApi {
    @GET("orders")
    suspend fun getMyOrders(): BaseResponse<List<OrderDto>>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: Int): BaseResponse<OrderDto>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): BaseResponse<OrderDto>

    @POST("orders/checkout")
    suspend fun checkout(@Body request: CheckoutRequest): BaseResponse<String>
}
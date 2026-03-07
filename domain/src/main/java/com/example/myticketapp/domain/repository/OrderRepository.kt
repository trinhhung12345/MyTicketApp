package com.example.myticketapp.domain.repository

import com.example.myticketapp.domain.model.CreateOrderPayload
import com.example.myticketapp.domain.model.Order
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getMyOrders(): Flow<Resource<List<Order>>>
    fun getOrderById(id: Int): Flow<Resource<Order>>
    fun createOrder(payload: CreateOrderPayload): Flow<Resource<Order>>
    fun checkout(orderId: Int): Flow<Resource<String>>
}
package com.example.myticketapp.data.repository

import com.example.myticketapp.data.remote.api.OrderApi
import com.example.myticketapp.data.remote.dto.CheckoutRequest
import com.example.myticketapp.data.remote.dto.toDomain
import com.example.myticketapp.data.remote.dto.toDto
import com.example.myticketapp.domain.model.CreateOrderPayload
import com.example.myticketapp.domain.model.Order
import com.example.myticketapp.domain.repository.OrderRepository
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val api: OrderApi
) : OrderRepository {

    override fun getMyOrders(): Flow<Resource<List<Order>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getMyOrders()
            if (response.code == 200) {
                emit(Resource.Success(response.data.orEmpty().map { it.toDomain() }))
            } else {
                emit(Resource.Error(response.message.ifBlank { "Lỗi tải danh sách đơn hàng" }))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối máy chủ"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối Internet. Vui lòng kiểm tra mạng!"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Đã xảy ra lỗi tải danh sách đơn hàng"))
        }
    }

    override fun getOrderById(id: Int): Flow<Resource<Order>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getOrderById(id)
            if (response.code == 200 && response.data != null) {
                emit(Resource.Success(response.data.toDomain()))
            } else {
                emit(Resource.Error(response.message.ifBlank { "Lỗi tải chi tiết đơn hàng" }))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối máy chủ"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối Internet. Vui lòng kiểm tra mạng!"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Đã xảy ra lỗi tải chi tiết đơn hàng"))
        }
    }

    override fun createOrder(payload: CreateOrderPayload): Flow<Resource<Order>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.createOrder(payload.toDto())
            if (response.code == 200 && response.data != null) {
                emit(Resource.Success(response.data.toDomain()))
            } else {
                emit(Resource.Error(response.message.ifBlank { "Lỗi tạo đơn hàng" }))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối máy chủ"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối Internet. Vui lòng kiểm tra mạng!"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Đã xảy ra lỗi tạo đơn hàng"))
        }
    }

    override fun checkout(orderId: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.checkout(CheckoutRequest(orderId))
            if (response.code == 200) {
                val paymentUrl = response.data?.takeIf { it.isNotBlank() }
                    ?: response.message.takeIf { it.isNotBlank() }

                if (paymentUrl != null) {
                    emit(Resource.Success(paymentUrl))
                } else {
                    emit(Resource.Error("Không lấy được link thanh toán"))
                }
            } else {
                emit(Resource.Error(response.message.ifBlank { "Không lấy được link thanh toán" }))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối máy chủ"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối Internet. Vui lòng kiểm tra mạng!"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Đã xảy ra lỗi thanh toán"))
        }
    }
}
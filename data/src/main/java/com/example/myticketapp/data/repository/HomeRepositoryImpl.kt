package com.example.myticketapp.data.repository

import android.util.Log
import com.example.myticketapp.data.remote.api.HomeApi
import com.example.myticketapp.data.remote.dto.toDomain
import com.example.myticketapp.domain.model.Category
import com.example.myticketapp.domain.model.Event
import com.example.myticketapp.domain.repository.HomeRepository
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

private const val TAG = "HomeRepositoryImpl"

class HomeRepositoryImpl @Inject constructor(
    private val api: HomeApi
) : HomeRepository {
    override fun getCategories(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getCategories()
            Log.d(TAG, "getCategories response: code=${response.code}, message=${response.message}")
            when (response.code) {
                200 -> {
                    emit(Resource.Success(response.data?.map { it.toDomain() } ?: emptyList()))
                }
                401 -> {
                    Log.d(TAG, "getCategories: TOKEN_EXPIRED (response.code)")
                    emit(Resource.Error("TOKEN_EXPIRED"))
                }
                else -> emit(Resource.Error(response.message ?: "Lỗi tải danh mục"))
            }
        } catch (e: HttpException) {
            Log.d(TAG, "getCategories HttpException: code=${e.code()}, message=${e.message}")
            if (e.code() == 401) {
                Log.d(TAG, "getCategories: TOKEN_EXPIRED (HttpException)")
                emit(Resource.Error("TOKEN_EXPIRED"))
            } else {
                emit(Resource.Error(e.message ?: "Lỗi HTTP"))
            }
        } catch (e: Exception) {
            Log.d(TAG, "getCategories Exception: ${e.message}")
            emit(Resource.Error(e.message ?: "Lỗi mạng"))
        }
    }

    override fun getEvents(): Flow<Resource<List<Event>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getEvents()
            Log.d(TAG, "getEvents response: code=${response.code}, message=${response.message}")
            when (response.code) {
                200 -> {
                    // Gọi hàm .toDomain() để chạy logic bóc tách ảnh/giá/ngày
                    emit(Resource.Success(response.data?.map { it.toDomain() } ?: emptyList()))
                }
                401 -> {
                    Log.d(TAG, "getEvents: TOKEN_EXPIRED (response.code)")
                    emit(Resource.Error("TOKEN_EXPIRED"))
                }
                else -> emit(Resource.Error(response.message ?: "Lỗi tải sự kiện"))
            }
        } catch (e: HttpException) {
            Log.d(TAG, "getEvents HttpException: code=${e.code()}, message=${e.message}")
            if (e.code() == 401) {
                Log.d(TAG, "getEvents: TOKEN_EXPIRED (HttpException)")
                emit(Resource.Error("TOKEN_EXPIRED"))
            } else {
                emit(Resource.Error(e.message ?: "Lỗi HTTP"))
            }
        } catch (e: Exception) {
            Log.d(TAG, "getEvents Exception: ${e.message}")
            emit(Resource.Error(e.message ?: "Lỗi mạng"))
        }
    }

    override fun getEventDetail(id: Int): Flow<Resource<Event>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getEventDetail(id)
            Log.d(TAG, "getEventDetail response: code=${response.code}, message=${response.message}")
            when (response.code) {
                200 -> {
                    if (response.data != null) {
                        emit(Resource.Success(response.data.toDomain()))
                    } else {
                        emit(Resource.Error("Dữ liệu trống"))
                    }
                }
                401 -> {
                    // Đánh dấu lỗi 401 để UI biết đường đá văng ra Login
                    Log.d(TAG, "getEventDetail: TOKEN_EXPIRED (response.code)")
                    emit(Resource.Error("TOKEN_EXPIRED"))
                }
                else -> emit(Resource.Error(response.message ?: "Lỗi không xác định"))
            }
        } catch (e: HttpException) {
            Log.d(TAG, "getEventDetail HttpException: code=${e.code()}, message=${e.message}")
            if (e.code() == 401) {
                Log.d(TAG, "getEventDetail: TOKEN_EXPIRED (HttpException)")
                emit(Resource.Error("TOKEN_EXPIRED"))
            } else {
                emit(Resource.Error(e.message ?: "Lỗi HTTP"))
            }
        } catch (e: Exception) {
            Log.d(TAG, "getEventDetail Exception: ${e.message}")
            emit(Resource.Error(e.message ?: "Lỗi mạng"))
        }
    }
}
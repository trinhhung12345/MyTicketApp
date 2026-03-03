package com.example.myticketapp.data.repository

import com.example.myticketapp.data.remote.api.HomeApi
import com.example.myticketapp.data.remote.dto.toDomain
import com.example.myticketapp.domain.model.Category
import com.example.myticketapp.domain.model.Event
import com.example.myticketapp.domain.repository.HomeRepository
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val api: HomeApi
) : HomeRepository {
    override fun getCategories(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getCategories()
            if (response.code == 200) {
                emit(Resource.Success(response.data?.map { it.toDomain() } ?: emptyList()))
            } else emit(Resource.Error("Lỗi tải danh mục"))
        } catch (e: Exception) { emit(Resource.Error(e.message ?: "Lỗi mạng")) }
    }

    override fun getEvents(): Flow<Resource<List<Event>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getEvents()
            if (response.code == 200) {
                // Gọi hàm .toDomain() để chạy logic bóc tách ảnh/giá/ngày
                emit(Resource.Success(response.data?.map { it.toDomain() } ?: emptyList()))
            } else emit(Resource.Error("Lỗi tải sự kiện"))
        } catch (e: Exception) { emit(Resource.Error(e.message ?: "Lỗi mạng")) }
    }
}
package com.example.myticketapp.data.repository

import com.example.myticketapp.data.remote.api.AuthApi
import com.example.myticketapp.data.remote.dto.LoginRequest
import com.example.myticketapp.data.remote.dto.toDomainModel
import com.example.myticketapp.domain.model.User
import com.example.myticketapp.domain.repository.AuthRepository
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : AuthRepository {

    override fun login(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.login(LoginRequest(email, password))

            if (response.code == 200 && response.data != null) {
                // Chuyển DTO thành Domain Model
                emit(Resource.Success(response.data.toDomainModel()))
            } else {
                emit(Resource.Error(response.message ?: "Lỗi không xác định từ server"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối máy chủ"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối Internet. Vui lòng kiểm tra mạng!"))
        } catch (e: Exception) {
            emit(Resource.Error("Đã xảy ra lỗi: ${e.message}"))
        }
    }
}
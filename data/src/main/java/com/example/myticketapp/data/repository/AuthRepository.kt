package com.example.myticketapp.data.repository

import com.example.myticketapp.data.local.TokenDataStore
import com.example.myticketapp.data.remote.api.AuthApi
import com.example.myticketapp.data.remote.dto.ForgotPasswordRequest
import com.example.myticketapp.data.remote.dto.LoginRequest
import com.example.myticketapp.data.remote.dto.RegisterRequestDto
import com.example.myticketapp.data.remote.dto.SendOtpRequest
import com.example.myticketapp.data.remote.dto.toDomainModel
import com.example.myticketapp.domain.model.RegisterRequest
import com.example.myticketapp.domain.model.User
import com.example.myticketapp.domain.repository.AuthRepository
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    override fun login(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.login(LoginRequest(email, password))

            if (response.code == 200 && response.data != null) {
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

    override fun forgotPassword(email: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.forgotPassword(ForgotPasswordRequest(email))
            if (response.code == 200) {
                emit(Resource.Success(response.message.ifBlank { "New password has been sent to your email" }))
            } else {
                emit(Resource.Error(response.message.ifBlank { "Gửi yêu cầu quên mật khẩu thất bại" }))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối máy chủ"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối Internet. Vui lòng kiểm tra mạng!"))
        } catch (e: Exception) {
            emit(Resource.Error("Đã xảy ra lỗi: ${e.message}"))
        }
    }

    override fun sendOtp(phone: String, email: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.sendOtp(SendOtpRequest(phone, email))
            if (response.code == 200) {
                emit(Resource.Success("OTP đã được gửi"))
            } else {
                emit(Resource.Error(response.message ?: "Lỗi gửi OTP"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối máy chủ"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối Internet. Vui lòng kiểm tra mạng!"))
        } catch (e: Exception) {
            emit(Resource.Error("Đã xảy ra lỗi: ${e.message}"))
        }
    }

    override fun register(request: RegisterRequest): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val dto = RegisterRequestDto(
                accountPhone = request.accountPhone,
                email = request.email,
                accountName = request.accountName,
                password = request.password,
                confirmPassword = request.confirmPassword,
                code = request.code,
                type = request.type,
                businessRole = request.businessRole,
                purpose = request.purpose
            )
            val response = api.register(dto)
            if (response.code == 200 && response.data != null) {
                emit(Resource.Success(response.data.toDomainModel()))
            } else {
                emit(Resource.Error(response.message ?: "Đăng ký thất bại"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối máy chủ"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối Internet. Vui lòng kiểm tra mạng!"))
        } catch (e: Exception) {
            emit(Resource.Error("Đã xảy ra lỗi: ${e.message}"))
        }
    }

    override suspend fun saveToken(token: String, email: String) {
        tokenDataStore.saveToken(token, email)
    }

    override suspend fun clearToken() {
        tokenDataStore.clearToken()
    }
}
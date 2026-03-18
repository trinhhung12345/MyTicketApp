package com.example.myticketapp.data.repository

import com.example.myticketapp.data.remote.api.UserApi
import com.example.myticketapp.data.remote.dto.ChangePasswordRequestDto
import com.example.myticketapp.data.remote.dto.UpdateUserProfileRequestDto
import com.example.myticketapp.data.remote.dto.toDomain
import com.example.myticketapp.domain.model.ChangePasswordRequest
import com.example.myticketapp.domain.model.UpdateUserProfileRequest
import com.example.myticketapp.domain.model.UserProfile
import com.example.myticketapp.domain.repository.UserRepository
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation của UserRepository
 */
class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
) : UserRepository {

    override fun getMyProfile(): Flow<Resource<UserProfile>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getMyProfile()

            if (response.code == 200 && response.data != null) {
                emit(Resource.Success(response.data.toDomain()))
            } else {
                // Kiểm tra nếu lỗi 401 - Unauthorized (token hết hạn)
                if (response.code == 401) {
                    emit(Resource.Error("TOKEN_EXPIRED"))
                } else {
                    emit(Resource.Error(response.message ?: "Lỗi tải thông tin profile"))
                }
            }
        } catch (e: HttpException) {
            if (e.code() == 401) {
                emit(Resource.Error("TOKEN_EXPIRED"))
            } else {
                emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối máy chủ"))
            }
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối Internet. Vui lòng kiểm tra mạng!"))
        } catch (e: Exception) {
            emit(Resource.Error("Đã xảy ra lỗi: ${e.message}"))
        }
    }

    override fun updateMyProfile(request: UpdateUserProfileRequest): Flow<Resource<UserProfile>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.updateMyProfile(
                UpdateUserProfileRequestDto(
                    name = request.name,
                    email = request.email,
                    phone = request.phone,
                    address = request.address,
                    birthday = request.birthday
                )
            )

            if (response.code == 200 && response.data != null) {
                emit(Resource.Success(response.data.toDomain()))
            } else {
                if (response.code == 401) {
                    emit(Resource.Error("TOKEN_EXPIRED"))
                } else {
                    emit(Resource.Error(response.message ?: "Cập nhật thông tin thất bại"))
                }
            }
        } catch (e: HttpException) {
            if (e.code() == 401) {
                emit(Resource.Error("TOKEN_EXPIRED"))
            } else {
                emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối máy chủ"))
            }
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối Internet. Vui lòng kiểm tra mạng!"))
        } catch (e: Exception) {
            emit(Resource.Error("Đã xảy ra lỗi: ${e.message}"))
        }
    }

    override fun changePassword(request: ChangePasswordRequest): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.changePassword(
                ChangePasswordRequestDto(
                    oldPassword = request.oldPassword,
                    newPassword = request.newPassword,
                    confirmPassword = request.confirmPassword
                )
            )

            if (response.code == 200) {
                emit(Resource.Success(response.message.ifBlank { "Đổi mật khẩu thành công" }))
            } else {
                if (response.code == 401) {
                    emit(Resource.Error("TOKEN_EXPIRED"))
                } else {
                    emit(Resource.Error(response.message.ifBlank { "Đổi mật khẩu thất bại" }))
                }
            }
        } catch (e: HttpException) {
            if (e.code() == 401) {
                emit(Resource.Error("TOKEN_EXPIRED"))
            } else {
                emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối máy chủ"))
            }
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối Internet. Vui lòng kiểm tra mạng!"))
        } catch (e: Exception) {
            emit(Resource.Error("Đã xảy ra lỗi: ${e.message}"))
        }
    }
}

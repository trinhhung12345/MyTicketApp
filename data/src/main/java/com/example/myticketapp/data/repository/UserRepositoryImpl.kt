package com.example.myticketapp.data.repository

import com.example.myticketapp.data.remote.api.UserApi
import com.example.myticketapp.data.remote.dto.toDomain
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
}

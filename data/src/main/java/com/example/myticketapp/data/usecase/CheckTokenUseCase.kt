package com.example.myticketapp.data.usecase

import com.example.myticketapp.data.local.TokenDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UseCase kiểm tra xem đã có token lưu trữ chưa
 * Dùng để xác định có cần đăng nhập lại không
 */
@Singleton
class CheckTokenUseCase @Inject constructor(
    private val tokenDataStore: TokenDataStore
) {
    /**
     * Trả về Flow<Boolean> cho biết đã có token chưa
     */
    operator fun invoke(): Flow<Boolean> = tokenDataStore.hasToken

    /**
     * Lấy email đã lưu (nếu có)
     */
    suspend fun getSavedEmail(): String? = tokenDataStore.userEmail.first()
}

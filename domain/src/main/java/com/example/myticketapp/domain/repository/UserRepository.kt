package com.example.myticketapp.domain.repository

import com.example.myticketapp.domain.model.UserProfile
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface cho các thao tác liên quan đến User Profile
 */
interface UserRepository {
    /**
     * Lấy thông tin profile của user hiện tại từ API
     */
    fun getMyProfile(): Flow<Resource<UserProfile>>
}

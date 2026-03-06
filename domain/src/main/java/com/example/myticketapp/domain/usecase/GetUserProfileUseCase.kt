package com.example.myticketapp.domain.usecase

import com.example.myticketapp.domain.model.UserProfile
import com.example.myticketapp.domain.repository.UserRepository
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase lấy thông tin profile của user hiện tại
 */
class GetUserProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<Resource<UserProfile>> {
        return repository.getMyProfile()
    }
}

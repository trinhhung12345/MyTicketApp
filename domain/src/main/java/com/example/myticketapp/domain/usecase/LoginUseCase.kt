package com.example.myticketapp.domain.usecase

import com.example.myticketapp.domain.repository.AuthRepository
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<Resource<com.example.myticketapp.domain.model.User>> {
        return repository.login(email, password)
    }
}
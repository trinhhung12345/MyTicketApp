package com.example.myticketapp.domain.usecase

import com.example.myticketapp.domain.model.RegisterRequest
import com.example.myticketapp.domain.model.User
import com.example.myticketapp.domain.repository.AuthRepository
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(request: RegisterRequest): Flow<Resource<User>> {
        return repository.register(request)
    }
}

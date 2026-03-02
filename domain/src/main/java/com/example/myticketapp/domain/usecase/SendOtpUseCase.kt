package com.example.myticketapp.domain.usecase

import com.example.myticketapp.domain.repository.AuthRepository
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(phone: String, email: String): Flow<Resource<String>> {
        return repository.sendOtp(phone, email)
    }
}

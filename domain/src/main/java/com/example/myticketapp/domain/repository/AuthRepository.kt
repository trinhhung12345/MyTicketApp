package com.example.myticketapp.domain.repository

import com.example.myticketapp.domain.model.User
import com.example.myticketapp.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<User>>
}
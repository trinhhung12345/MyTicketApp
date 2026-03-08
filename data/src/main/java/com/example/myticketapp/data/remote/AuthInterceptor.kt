package com.example.myticketapp.data.remote

import com.example.myticketapp.data.local.TokenDataStore
import com.example.myticketapp.domain.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor tự động gắn Bearer Token vào Header Authorization
 * cho mọi request gửi đến server.
 * Đồng thời phát hiện 401 và thông báo SessionManager.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val token = runBlocking {
            tokenDataStore.accessToken.first()
        }

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            sessionManager.onSessionExpired()
        }

        return response
    }
}

package com.example.myticketapp.domain.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton phát sự kiện khi phát hiện token hết hạn (HTTP 401).
 * Được observe bởi UI (NavGraph) để hiển thị dialog và chuyển về Login.
 */
@Singleton
class SessionManager @Inject constructor() {

    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent = _sessionExpiredEvent.asSharedFlow()

    fun onSessionExpired() {
        _sessionExpiredEvent.tryEmit(Unit)
    }
}

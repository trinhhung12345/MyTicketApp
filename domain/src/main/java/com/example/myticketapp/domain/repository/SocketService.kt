package com.example.myticketapp.domain.repository

/**
 * Service quản lý kết nối WebSocket real-time
 */
interface SocketService {
    fun connectAndSubscribe()
    fun disconnect()
}

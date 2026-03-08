package com.example.myticketapp.data.remote.socket

import android.util.Log
import com.example.myticketapp.data.local.TokenDataStore
import com.example.myticketapp.data.local.room.NotificationDao
import com.example.myticketapp.data.local.room.NotificationEntity
import com.example.myticketapp.domain.repository.SocketService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class StompMessageDto(
    val id: String?,
    val eventId: String?,
    val title: String?,
    val message: String?,
    val createdAt: String?
)

/**
 * Implementation của SocketService sử dụng STOMP over SockJS
 */
class StompSocketManager(
    private val okHttpClient: OkHttpClient,
    private val notificationDao: NotificationDao,
    private val tokenDataStore: TokenDataStore,
    private val wsUrl: String
) : SocketService {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var stompSession: StompSession? = null
    private val gson = Gson()

    override fun connectAndSubscribe() {
        scope.launch {
            try {
                val token = tokenDataStore.accessToken.first()
                val roleName = tokenDataStore.userRole.first()

                if (token.isNullOrEmpty()) {
                    Log.w("STOMP", "No token, skipping connection")
                    return@launch
                }

                Log.d("STOMP", "Connecting to $wsUrl with role $roleName")

                val rawWsClient = OkHttpWebSocketClient(okHttpClient)
                val wsClient = SockJsWebSocketClient(rawWsClient)
                val stompClient = StompClient(wsClient)

                val customHeaders = mapOf("Authorization" to "Bearer $token")
                stompSession = stompClient.connect(
                    url = wsUrl,
                    customStompConnectHeaders = customHeaders
                )
                Log.d("STOMP", "Connected successfully")

                val topic = if (roleName.uppercase() == "SUPER_ADMIN" || roleName.uppercase() == "ADMIN") {
                    "/topic/events/super-admins"
                } else {
                    "/topic/events/users"
                }

                Log.d("STOMP", "Subscribing to topic: $topic")

                val subscription = stompSession?.subscribe(
                    StompSubscribeHeaders(destination = topic)
                )

                subscription?.collect { message ->
                    val jsonBody = message.bodyAsText
                    Log.d("STOMP", "Received message: $jsonBody")

                    try {
                        val dto = gson.fromJson(jsonBody, StompMessageDto::class.java)
                        val eventId = dto.eventId ?: dto.id ?: "0"
                        val timestamp = dto.createdAt ?: SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss",
                            Locale.getDefault()
                        ).format(Date())

                        val entity = NotificationEntity(
                            id = "${eventId}-${System.currentTimeMillis()}",
                            eventId = eventId,
                            title = dto.title ?: "Sự kiện mới",
                            message = dto.message ?: "Có cập nhật mới",
                            createdAt = timestamp,
                            isRead = false
                        )
                        notificationDao.insertNotification(entity)
                        Log.d("STOMP", "Notification saved to DB: ${entity.title}")

                    } catch (e: Exception) {
                        Log.e("STOMP", "Parse error", e)
                    }
                }

            } catch (e: Exception) {
                Log.e("STOMP", "Connection error", e)
                scope.launch {
                    delay(5000)
                    Log.d("STOMP", "Attempting to reconnect...")
                    connectAndSubscribe()
                }
            }
        }
    }

    override fun disconnect() {
        scope.launch {
            try {
                stompSession?.disconnect()
                stompSession = null
                Log.d("STOMP", "Disconnected")
            } catch (e: Exception) {
                Log.e("STOMP", "Error disconnecting", e)
            }
        }
    }
}

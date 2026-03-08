package com.example.myticketapp.data.remote.socket

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.io.bytestring.ByteString
import org.hildan.krossbow.websocket.WebSocketClient
import org.hildan.krossbow.websocket.WebSocketConnection
import org.hildan.krossbow.websocket.WebSocketFrame
import org.json.JSONArray
import kotlin.random.Random

/**
 * WebSocketClient adapter thêm SockJS transport layer.
 *
 * Backend Spring Boot dùng SockJS, nên client cần:
 * 1. Kết nối tới URL dạng: {endpoint}/{serverNum}/{sessionId}/websocket
 * 2. Bọc message gửi đi trong JSON array: ["content"]
 * 3. Tách message nhận từ SockJS frame: a["content"] → content
 * 4. Bỏ qua SockJS open frame (o) và heartbeat (h)
 */
class SockJsWebSocketClient(
    private val delegate: WebSocketClient
) : WebSocketClient {

    override val supportsCustomHeaders: Boolean get() = delegate.supportsCustomHeaders

    override suspend fun connect(
        url: String,
        protocols: List<String>,
        headers: Map<String, String>
    ): WebSocketConnection {
        val serverNum = Random.nextInt(0, 1000)
        val sessionId = generateSessionId()
        val sockJsUrl = "${url.trimEnd('/')}/$serverNum/$sessionId/websocket"
        Log.d("SockJS", "Connecting to: $sockJsUrl")
        val rawConnection = delegate.connect(sockJsUrl, protocols, headers)
        return SockJsWebSocketConnection(rawConnection)
    }

    private fun generateSessionId(): String {
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
        return buildString {
            repeat(8) { append(chars[Random.nextInt(chars.length)]) }
        }
    }
}

private class SockJsWebSocketConnection(
    private val delegate: WebSocketConnection
) : WebSocketConnection {

    override val url: String get() = delegate.url
    override val protocol: String? get() = delegate.protocol
    override val canSend: Boolean get() = delegate.canSend

    override val incomingFrames: Flow<WebSocketFrame> =
        delegate.incomingFrames.mapNotNull { frame ->
            when (frame) {
                is WebSocketFrame.Text -> processSockJsFrame(frame.text)
                else -> frame
            }
        }

    override suspend fun sendText(frameText: String) {
        val jsonArray = JSONArray()
        jsonArray.put(frameText)
        delegate.sendText(jsonArray.toString())
    }

    override suspend fun sendBinary(frameData: ByteString) {
        delegate.sendBinary(frameData)
    }

    override suspend fun close(code: Int, reason: String?) {
        delegate.close(code, reason)
    }

    private fun processSockJsFrame(text: String): WebSocketFrame? {
        return when {
            text == "o" -> {
                Log.d("SockJS", "Open frame received")
                null
            }
            text == "h" -> {
                Log.d("SockJS", "Heartbeat received")
                null
            }
            text.startsWith("a") -> {
                try {
                    val jsonArray = JSONArray(text.substring(1))
                    if (jsonArray.length() > 0) {
                        val content = jsonArray.getString(0)
                        WebSocketFrame.Text(content)
                    } else null
                } catch (e: Exception) {
                    Log.e("SockJS", "Error parsing message frame: $text", e)
                    null
                }
            }
            text.startsWith("c") -> {
                Log.d("SockJS", "Close frame: $text")
                null
            }
            else -> {
                Log.d("SockJS", "Unknown frame: $text")
                WebSocketFrame.Text(text)
            }
        }
    }
}

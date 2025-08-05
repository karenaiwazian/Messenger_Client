package com.aiwazian.messenger.utils

import android.util.Log
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.data.MessageWrapper
import com.aiwazian.messenger.data.WebSocketMessage
import com.aiwazian.messenger.services.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.time.Instant
import java.util.Date

object WebSocketManager {
    private const val SOCKET_URL = Constants.WEB_SOCKET_URL
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private val _isConnectedState = MutableStateFlow(false)
    val isConnectedState = _isConnectedState.asStateFlow()

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    var onConnect: (() -> Unit)? = null
    var onReceiveMessage: ((Message) -> Unit)? = null
    var onClose: ((Int) -> Unit)? = null
    var onFailure: (() -> Unit)? = null

    fun connect() {
        if (_isConnectedState.value){
            return
        }

        val token = TokenManager.getToken()


        val request = Request.Builder()
            .url("$SOCKET_URL?token=$token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _isConnectedState.value = true

                onConnect?.invoke()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val webSocketMessage = json.decodeFromString<WebSocketMessage>(text)

                    when (webSocketMessage.action) {
                        "NEW_MESSAGE" -> {
                            val messageWrapper = json.decodeFromString<MessageWrapper>(
                                json.encodeToString(webSocketMessage.data)
                            )

                            val content = messageWrapper.message

                            val message = Message(
                                id = content.id,
                                senderId = content.senderId,
                                chatId = content.chatId,
                                messageId = content.id,
                                text = content.text,
                                sendTime = content.sendTime
                            )

                            onReceiveMessage?.invoke(message)
                        }

                        else -> {
                            Log.w("wss", "Неизвестный тип сообщения: ${webSocketMessage.action}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("wss", "Ошибка парсинга: ${e.message}")
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(code, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _isConnectedState.value = false

                onClose?.invoke(code)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _isConnectedState.value = false

                onFailure?.invoke()
            }
        })
    }
}

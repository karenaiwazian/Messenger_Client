package com.aiwazian.messenger.utils

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.aiwazian.messenger.LoginActivity
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.data.WebSocketMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object WebSocketManager {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private const val SOCKET_URL = Constants.WEB_SOCKET_URL

    private val _isConnectedState = MutableStateFlow(false)
    val isConnectedState = _isConnectedState.asStateFlow()

    var onConnect: (() -> Unit)? = null
    var onMessage: (Message) -> Unit = { }
    var onClose: (() -> Unit)? = null

    lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun connect() {
        if (_isConnectedState.value) return

        val token = UserManager.token

        if (token.isBlank()) {
            return
        }

        val request = Request.Builder()
            .url("$SOCKET_URL?token=$token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                _isConnectedState.value = true
                onConnect?.invoke()
                Log.d("wss", "Подключено")
            }

            override fun onMessage(ws: WebSocket, text: String) {
                try {
                    val message = Json.Default.decodeFromString<Message>(text)
                    onMessage(message)
                    Log.d("wss", "Получено сообщение: $message")
                } catch (e: Exception) {
                    Log.e("wss", "Ошибка парсинга: ${e.message}")
                }
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                Log.d("wss", "Закрытие: $code $reason")
                ws.close(code, null)
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                _isConnectedState.value = false

                if (code != 1008 && code != 1000) {
                    retryConnection()
                } else if (::appContext.isInitialized) {
                    val store = DataStoreManager.getInstance()
                    CoroutineScope(Dispatchers.IO).launch {
                        store.saveToken("")
                    }

                    Handler(Looper.getMainLooper()).post {
                        val intent = Intent(appContext, LoginActivity::class.java).apply {
                            //TODO Intent.setFlag = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        appContext.startActivity(intent)
                    }
                }
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e("wss", "⚠️ Ошибка подключения: ${t.message}")
                _isConnectedState.value = false
                retryConnection()
            }
        })
    }

    private fun retryConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            connect()
        }
    }

    fun sendMessage(message: Message) {
        try {
            val json = Json.Default.encodeToString(message)
            webSocket?.send(json)
        } catch (e: Exception) {
            Log.e("wss", "Ошибка сериализации: ${e.message}", e)
        }
    }

    fun send(data: WebSocketMessage) {
        try {
            val json = Json.Default.encodeToString(data)
            webSocket?.send(json)
        } catch (e: Exception) {
            Log.e("wss", "Ошибка сериализации: ${e.message}", e)
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Выход")
        _isConnectedState.value = false
        onClose?.invoke()
    }
}
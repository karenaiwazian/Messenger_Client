package com.aiwazian.messenger.utils

import android.content.Context
import android.content.Intent
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
    private const val SOCKET_URL = Constants.WEB_SOCKET_URL
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private val _isConnectedState = MutableStateFlow(false)
    val isConnectedState = _isConnectedState.asStateFlow()

    var onConnect: (() -> Unit)? = null
    var onReceiveMessage: (Message) -> Unit = { }
    var onSendMessage: (Message) -> Unit = { }

    lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun connect() {
        if (_isConnectedState.value) return

        val token = UserManager.token

        val request = Request.Builder()
            .url("$SOCKET_URL?token=$token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _isConnectedState.value = true
                onConnect?.invoke()
                Log.d("wss", "Подключено")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val message = Json.Default.decodeFromString<Message>(text)
                    onReceiveMessage(message)
                    Log.d("wss", "Получено сообщение: $message")
                } catch (e: Exception) {
                    Log.e("wss", "Ошибка парсинга: ${e.message}")
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("wss", "Закрытие: $code $reason")
                webSocket.close(code, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _isConnectedState.value = false

                if (code != 1008 && code != 1000) {
                    retryConnection()
                } else if (::appContext.isInitialized) {
                    val store = DataStoreManager.getInstance()
                    CoroutineScope(Dispatchers.IO).launch {
                        store.saveToken("")
                    }

                    val intent = Intent(appContext, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    appContext.startActivity(intent)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("wss", "⚠️ Ошибка подключения: ${t.message}")
                _isConnectedState.value = false
                retryConnection()
            }
        })
    }

    fun sendMessage(message: Message) {
        try {
            val json = Json.Default.encodeToString(message)
            webSocket?.send(json)
            onSendMessage(message)
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

    private fun retryConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            connect()
        }
    }
}

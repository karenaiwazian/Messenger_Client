package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.utils.ChatStateManager
import com.aiwazian.messenger.utils.WebSocketManager
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.services.TokenManager

class ChatViewModel(
    private val chatId: Int,
    private val currentUserId: Int,
) : ViewModel() {

    var messageText by mutableStateOf("")
        private set

    var messages = mutableStateListOf<Message>()
        private set

    init {
        WebSocketManager.onReceiveMessage = { message ->
            if (ChatStateManager.isChatOpen(message.senderId) && message.senderId != currentUserId) {
                messages.add(message)
            }
        }
    }

    fun changeText(newText: String) {
        messageText = newText
    }

    suspend fun loadMessages() {
        try {
            val tokenManager = TokenManager()
            val token = tokenManager.getToken()
            val response = RetrofitInstance.api.getMessagesBetweenUsers("Bearer $token",chatId)

            if (response.isSuccessful) {
                messages.addAll(response.body().orEmpty())
            } else {
                Log.e("ChatVM", "Ошибка ответа загрузки сообщений: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("ChatVM", "Ошибка загрузки сообщений: ${e.message}")
        }
    }

    suspend fun sendMessage() {
        if (messageText.isBlank()) {
            return
        }

        val message = Message(
            senderId = currentUserId,
            chatId = chatId,
            text = messageText
        )

        messages.add(message)
        messageText = ""

        try {
            val tokenManager = TokenManager()
            val token = tokenManager.getToken()
            val response =
                RetrofitInstance.api.sendMessage("Bearer $token", message)

            if (response.isSuccessful) {

            }
        } catch (e: Exception) {
            Log.e("ChatVM", "Ошибка отпаравки сррбщения: ${e.message}")
        }
    }

    fun deleteAllMessages() {
        messages.clear()
    }
}


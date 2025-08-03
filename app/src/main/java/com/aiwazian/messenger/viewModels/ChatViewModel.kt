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
            val response = RetrofitInstance.api.getMessagesBetweenUsers(chatId)

            if (response.isSuccessful) {
                messages.addAll(response.body().orEmpty())
            } else {
                Log.e("ChatVM", "Ошибка ответа загрузки сообщений: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("ChatVM", "Ошибка загрузки сообщений: ${e.message}")
        }
    }

    suspend fun sendMessage(): Message? {
        if (messageText.isBlank()) {
            return null
        }

        val message = Message(
            senderId = currentUserId,
            chatId = chatId,
            text = messageText
        )

        messages.add(message)
        messageText = ""

        try {
            val response =
                RetrofitInstance.api.sendMessage(message)

            if (response.isSuccessful) {
                return message
            }
            else {
                return null
            }
        } catch (e: Exception) {
            Log.e("ChatVM", "Ошибка отпаравки сррбщения: ${e.message}")
            return null
        }
    }

    fun deleteAllMessages() {
        messages.clear()
    }
}


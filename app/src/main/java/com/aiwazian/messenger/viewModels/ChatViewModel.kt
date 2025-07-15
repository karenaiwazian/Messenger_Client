package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.utils.ChatStateManager
import com.aiwazian.messenger.utils.UserManager
import com.aiwazian.messenger.utils.WebSocketManager
import com.aiwazian.messenger.api.RetrofitInstance
import kotlinx.coroutines.launch
import com.aiwazian.messenger.data.Message

class ChatViewModel(
    private val chatId: String,
    private val currentUserId: String,
) : ViewModel() {

    var messageText by mutableStateOf("")

    var messages = mutableStateListOf<Message>()
        private set

    init {
        loadMessages()
        WebSocketManager.onMessage = { message ->
            if (ChatStateManager.isChatOpen(message.senderId)) {
                messages.add(message)
            }
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            val token = UserManager.token

            try {
                val response = RetrofitInstance.api.getMessagesBetweenUsers(
                    token = "Bearer $token",
                    user1 = currentUserId,
                    user2 = chatId
                )

                if (response.isSuccessful) {
                    messages.addAll(response.body().orEmpty())
                } else {
                    Log.e("ChatVM", "Ошибка ответа: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ChatVM", "Ошибка запроса: ${e.message}")
            }
        }
    }

    fun sendMessage(text: String = messageText) {
        if (text.isBlank()) {
            return
        }

        val message = Message(
            senderId = currentUserId,
            receiverId = chatId,
            text = text.trim()
        )

        messages.add(message)

        WebSocketManager.sendMessage(message)
    }

    fun deleteAllMessages() {
        messages.clear()
    }
}


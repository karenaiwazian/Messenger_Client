package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.utils.ChatStateManager
import com.aiwazian.messenger.utils.WebSocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant

class ChatViewModel(
    private val chatId: Int,
    private val currentUserId: Int,
) : ViewModel() {
    
    private val _isVisibleDeleteChatDialog = MutableStateFlow(false)
    val isVisibleDeleteChatDialog = _isVisibleDeleteChatDialog.asStateFlow()
    
    private val _messageText = MutableStateFlow("")
    val messageText = _messageText.asStateFlow()
    
    var messages = mutableStateListOf<Message>()
        private set
    
    init {
        WebSocketManager.onReceiveMessage = { message ->
            if (ChatStateManager.isChatOpen(message.senderId) && message.senderId != currentUserId) {
                messages.add(message)
            }
        }
    }
    
    fun showDeleteChatDialog() {
        _isVisibleDeleteChatDialog.value = true
    }
    
    fun hideDeleteChatDialog() {
        _isVisibleDeleteChatDialog.value = false
    }
    
    fun changeText(newText: String) {
        _messageText.value = newText
    }
    
    suspend fun loadMessages() {
        try {
            val response = RetrofitInstance.api.getMessagesBetweenUsers(chatId)
            
            if (response.isSuccessful) {
                messages.addAll(response.body().orEmpty())
            } else {
                Log.e(
                    "ChatVM",
                    "Ошибка ответа загрузки сообщений: ${response.code()}"
                )
            }
        } catch (e: Exception) {
            Log.e(
                "ChatVM",
                "Ошибка загрузки сообщений: ${e.message}"
            )
        }
    }
    
    suspend fun sendMessage(): Message? {
        if (_messageText.value.isBlank()) {
            return null
        }
        
        val validText = _messageText.value.trim()
        
        val message = Message(
            id = messages.last().id + 1,
            messageId = messages.size + 1,
            senderId = currentUserId,
            chatId = chatId,
            text = validText,
            sendTime = System.currentTimeMillis()
        )
        
        messages.add(message)
        _messageText.value = ""
        
        try {
            val response = RetrofitInstance.api.sendMessage(message)
            
            return if (response.isSuccessful) {
                message
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(
                "ChatVM",
                "Ошибка отпаравки сррбщения: ${e.message}"
            )
            
            return null
        }
    }
    
    suspend fun deleteChat(deleteForReceiver: Boolean): Boolean {
        try {
            val response = RetrofitInstance.api.deleteChat(
                chatId = chatId,
                deleteForReceiver = deleteForReceiver
            )
            
            if (!response.isSuccessful) {
                return false
            }
            
            return response.code() == 200
        } catch (e: Exception) {
            Log.e(
                "DeleteChat",
                e.message.toString()
            )
            
            return false
        }
    }
    
    fun deleteAllMessages() {
        messages.clear()
    }
}


package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.customType.WebSocketAction
import com.aiwazian.messenger.data.ChatInfo
import com.aiwazian.messenger.data.DeleteChatPayload
import com.aiwazian.messenger.data.DeleteMessagePayload
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.services.ChatService
import com.aiwazian.messenger.services.DialogController
import com.aiwazian.messenger.services.UserManager
import com.aiwazian.messenger.utils.ChatState
import com.aiwazian.messenger.utils.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatService: ChatService) : ViewModel() {
    
    private val _currentUserId = UserManager.user.value.id
    
    private val _chatInfo = MutableStateFlow(ChatInfo())
    val chatInfo = _chatInfo.asStateFlow()
    
    private val _messageText = MutableStateFlow("")
    val messageText = _messageText.asStateFlow()
    
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()
    
    private val _selectedMessages = MutableStateFlow<Set<Message>>(emptySet())
    val selectedMessages = _selectedMessages.asStateFlow()
    
    val deleteChatDialog = DialogController()
    
    val deleteMessageDialog = DialogController()
    
    init {
        WebSocketManager.registerTypedMessageHandler<Message>(WebSocketAction.NEW_MESSAGE) { message ->
            if (_chatInfo.value.id == message.senderId && message.senderId != _currentUserId) {
                _messages.value += message
            }
        }
        
        WebSocketManager.registerTypedMessageHandler<DeleteChatPayload>(WebSocketAction.DELETE_CHAT) { chat ->
            if (chat.chatId == _chatInfo.value.id) {
                deleteAllMessages()
            }
        }
        
        WebSocketManager.registerTypedMessageHandler<DeleteMessagePayload>(WebSocketAction.DELETE_MESSAGE) { message ->
            if (_chatInfo.value.id == message.chatId) {
                deleteMessage(message.messageId)
            }
        }
    }
    
    fun changeText(newText: String) {
        _messageText.value = newText
    }
    
    fun selectMessage(message: Message) {
        _selectedMessages.value += message
    }
    
    fun unselectMessage(message: Message) {
        _selectedMessages.value -= message
    }
    
    suspend fun openChat(chatId: Int) {
        ChatState.openChat(chatId)
        
        _chatInfo.update { chatInfo ->
            chatInfo.id = chatId
            chatInfo
        }
        
        try {
            val chatInfo = chatService.getChatInfo(chatId)
            
            if (chatInfo == null) {
                return
            }
            
            _chatInfo.value = chatInfo
            
            val chatMessages = chatService.getChatMessages(chatId)
            
            _messages.value = chatMessages.orEmpty()
        } catch (e: Exception) {
            Log.e(
                "ChatVM",
                "Ошибка загрузки сообщений: ${e.message}"
            )
        }
    }
    
    fun closeChat() {
        _chatInfo.update { ChatInfo() }
        
        ChatState.closeChat()
    }
    
    suspend fun sendMessage(): Message? {
        if (_messageText.value.isBlank()) {
            return null
        }
        
        val validText = _messageText.value.trim()
        
        val messageId = if (_messages.value.isNotEmpty()) {
            messages.value.last().id + 1
        } else {
            1
        }
        
        val message = Message(
            id = messageId,
            senderId = _currentUserId,
            chatId = _chatInfo.value.id,
            text = validText,
            sendTime = System.currentTimeMillis()
        )
        
        changeText("")
        
        _messages.update { it + message }
        
        try {
            val sentMessage = chatService.sendMessage(message)
            
            if (sentMessage == null) {
                return null
            }
            
            _messages.update { currentList ->
                currentList.map { message ->
                    if (message.id == messageId) {
                        message.copy(id = sentMessage.id)
                    } else {
                        message
                    }
                }
            }
            
            return sentMessage
        } catch (e: Exception) {
            Log.e(
                "ChatVM",
                "Ошибка отпаравки сррбщения: ${e.message}"
            )
            
            return null
        }
    }
    
    suspend fun tryDeleteMessage(
        messageId: Int,
        deleteForAll: Boolean
    ): Boolean {
        try {
            val isDeleted = chatService.deleteMessage(
                _chatInfo.value.id,
                messageId,
                deleteForAll
            )
            
            if (isDeleted) {
                deleteMessage(messageId)
            }
            
            return isDeleted
        } catch (e: Exception) {
            Log.e(
                "ChatVM",
                "Ошибка удаления сообщения",
                e
            )
            
            return false
        }
    }
    
    suspend fun tryDeleteChat(deleteForReceiver: Boolean): Boolean {
        try {
            val isDeleted = chatService.deleteChat(
                _chatInfo.value.id,
                deleteForReceiver
            )
            
            if (isDeleted) {
                deleteAllMessages()
            }
            
            return isDeleted
        } catch (e: Exception) {
            Log.e(
                "DeleteChat",
                e.message.toString()
            )
            
            return false
        }
    }
    
    private fun deleteMessage(messageId: Int) {
        val messages = _messages.value.filter { it.id != messageId }
        _messages.update { messages }
    }
    
    private fun deleteAllMessages() {
        _messages.update { emptyList() }
    }
}


package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.data.ChatInfo
import com.aiwazian.messenger.data.DeleteChatPayload
import com.aiwazian.messenger.data.DeleteMessagePayload
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.data.ReadMessagePayload
import com.aiwazian.messenger.database.repository.ChannelRepository
import com.aiwazian.messenger.database.repository.ChatRepository
import com.aiwazian.messenger.enums.ChatType
import com.aiwazian.messenger.enums.WebSocketAction
import com.aiwazian.messenger.interfaces.Profile
import com.aiwazian.messenger.services.DialogController
import com.aiwazian.messenger.services.UserManager
import com.aiwazian.messenger.utils.ChatState
import com.aiwazian.messenger.utils.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val channelRepository: ChannelRepository
) : ViewModel() {
    
    val myId = UserManager.user.value.id
    
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile = _profile.asStateFlow()
    
    private val _chatInfo = MutableStateFlow(ChatInfo())
    val chatInfo = _chatInfo.asStateFlow()
    
    private val _messageText = MutableStateFlow("")
    val messageText = _messageText.asStateFlow()
    
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()
    
    private val _selectedMessages = MutableStateFlow<Set<Message>>(emptySet())
    val selectedMessages = _selectedMessages.asStateFlow()
    
    val deleteChatDialog = DialogController()
    
    val clearHistoryDialog = DialogController()
    
    val deleteMessageDialog = DialogController()
    
    init {
        WebSocketManager.registerMessageHandler<Message>(WebSocketAction.NEW_MESSAGE) { message ->
            if (_chatInfo.value.id == message.senderId && message.senderId != myId) {
                _messages.update { it + message }
            }
        }
        
        WebSocketManager.registerMessageHandler<DeleteChatPayload>(WebSocketAction.DELETE_CHAT) { chat ->
            if (chat.chatId == _chatInfo.value.id) {
                deleteAllMessages()
            }
        }
        
        WebSocketManager.registerMessageHandler<DeleteMessagePayload>(WebSocketAction.DELETE_MESSAGE) { message ->
            if (_chatInfo.value.id == message.chatId) {
                deleteMessage(message.messageId)
            }
        }
        
        WebSocketManager.registerMessageHandler<ReadMessagePayload>(WebSocketAction.READ_MESSAGE) { message ->
            readMessage(message.messageId)
        }
    }
    
    fun changeText(newText: String) {
        _messageText.update { newText }
    }
    
    fun selectMessage(message: Message) {
        _selectedMessages.update { it + message }
    }
    
    fun unselectMessage(message: Message) {
        _selectedMessages.update { it - message }
    }
    
    suspend fun open(chatId: Int) {
        ChatState.openChat(chatId)
        
        _profile.update { null }
        
        _chatInfo.update { chatInfo ->
            chatInfo.copy(id = chatId)
        }
        
        val chatInfo = chatRepository.get(chatId)
        
        if (chatInfo == null) {
            _messages.update { emptyList() }
            return
        }
        
        _chatInfo.update { chatInfo }
        
        viewModelScope.launch {
            val chatMessages = chatRepository.getMessages(chatId)
            
            _messages.update { chatMessages }
        }
        
        if (chatInfo.chatType == ChatType.CHANNEL) {
            val channel = channelRepository.get(chatId)
            
            _profile.update { channel }
            
            return
        }
    }
    
    fun close() {
        _chatInfo.update { ChatInfo() }
        _messages.update { emptyList() }
        
        ChatState.closeChat()
    }
    
    suspend fun sendMessage(): Message? {
        if (_messageText.value.isBlank()) {
            return null
        }
        
        val validText = _messageText.value.trim()
        
        val messageId = _messages.value.let {
            if (it.isNotEmpty()) {
                it.last().id + 1
            } else {
                1
            }
        }
        
        val message = Message(
            id = messageId,
            senderId = myId,
            chatId = _chatInfo.value.id,
            text = validText,
            isRead = myId == _chatInfo.value.id,
            sendTime = System.currentTimeMillis()
        )
        
        changeText("")
        
        _messages.update { it + message }
        
        try {
            val sentMessage = chatRepository.sendMessage(message)
            
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
                "Ошибка отпаравки сррбщения",
                e
            )
            
            return null
        }
    }
    
    suspend fun markAsReadMessage(message: Message) {
        if (message.senderId == myId) {
            return
        }
        
        val isRead = chatRepository.makeAsRead(
            _chatInfo.value.id,
            message.id
        )
        
        if (isRead) {
            readMessage(message.id)
        }
    }
    
    suspend fun tryDeleteMessage(
        messageId: Int,
        deleteForAll: Boolean
    ): Boolean {
        try {
            val isDeleted = chatRepository.deleteMessage(
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
            val isDeleted = chatRepository.deleteChat(
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
                "Ошибка при удалении чата",
                e
            )
            
            return false
        }
    }
    
    suspend fun tryDeleteChatMessages(deleteForReceiver: Boolean): Boolean {
        try {
            val isDeleted = chatRepository.deleteChatMessages(
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
                "Ошибка при удалении сообщений в чате",
                e
            )
            
            return false
        }
    }
    
    private fun readMessage(messageId: Int) {
        _messages.update { currentList ->
            currentList.map { message ->
                if (message.id == messageId) {
                    val newMessage = message.copy(isRead = true)
                    newMessage
                } else {
                    message
                }
            }
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


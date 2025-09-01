package com.aiwazian.messenger.services

import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.ChatInfo
import com.aiwazian.messenger.data.Message
import javax.inject.Inject

class ChatService @Inject constructor() {
    
    suspend fun sendMessage(message: Message): Message? {
        val response = RetrofitInstance.api.sendMessage(message)
        return if (response.isSuccessful) response.body() else null
    }
    
    suspend fun getChatInfo(chatId: Int): ChatInfo? {
        val response = RetrofitInstance.api.getChatInfo(chatId)
        
        if (response.code() != 200) {
            return null
        }
        
        return response.body()
    }
    
    suspend fun getChatLastMessage(chatId: Int): Message? {
        val response = RetrofitInstance.api.getChatLastMessage(chatId)
        return response.body()
    }
    
    suspend fun getChatMessages(chatId: Int): List<Message>? {
        val response = RetrofitInstance.api.getMessagesBetweenUsers(chatId)
        return response.body()
    }
    
    suspend fun archiveChat(chatId: Int): Boolean {
        val response = RetrofitInstance.api.archiveChat(chatId)
        return response.isSuccessful
    }
    
    suspend fun unarchiveChat(chatId: Int): Boolean {
        val response = RetrofitInstance.api.unarchiveChat(chatId)
        return response.isSuccessful
    }
    
    suspend fun pinChat(
        chatId: Int,
        folderId: Int
    ): Boolean {
        val response = if (folderId == 0) {
            RetrofitInstance.api.pinChat(chatId)
        } else {
            RetrofitInstance.api.pinChatInFolder(
                folderId,
                chatId
            )
        }
        
        return response.isSuccessful
    }
    
    suspend fun unpinChat(
        chatId: Int,
        folderId: Int
    ): Boolean {
        val response = if (folderId == 0) {
            RetrofitInstance.api.unpinChat(chatId)
        } else {
            RetrofitInstance.api.unpinChatInFolder(
                folderId,
                chatId
            )
        }
        
        return response.isSuccessful
    }
    
    suspend fun deleteMessage(
        chatId: Int,
        messageId: Int,
        deleteForAll: Boolean
    ): Boolean {
        val response = RetrofitInstance.api.deleteMessage(
            chatId,
            messageId,
            deleteForAll
        )
        
        return response.isSuccessful
    }
    
    suspend fun deleteChat(
        chatId: Int,
        deleteForReceiver: Boolean
    ): Boolean {
        val response = RetrofitInstance.api.deleteChat(
            chatId,
            deleteForReceiver
        )
        
        return response.isSuccessful
    }
}
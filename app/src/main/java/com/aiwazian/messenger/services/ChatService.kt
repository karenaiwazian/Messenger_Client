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
        
        val responseChatInfo = response.body()?.toChatInto()
        
        return responseChatInfo
    }
    
    suspend fun makeAsReadMessage(
        chatId: Int,
        messageId: Int
    ): Boolean {
        val response = RetrofitInstance.api.makeAsReadMessage(
            chatId,
            messageId
        )
        
        return response.isSuccessful
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
    
    suspend fun pin(
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
    
    suspend fun unpin(
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
    
    suspend fun deleteChatMessages(
        chatId: Int,
        deleteForReceiver: Boolean
    ): Boolean {
        val response = RetrofitInstance.api.deleteChatMessages(
            chatId,
            deleteForReceiver
        )
        
        return response.isSuccessful
    }
}
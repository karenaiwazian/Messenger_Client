package com.aiwazian.messenger.services

import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.ChatFolder
import com.aiwazian.messenger.data.ChatInfo

class FolderService {

    suspend fun getFolders(): List<ChatFolder> {
        val tokenManager = TokenManager()
        val token = tokenManager.getToken()
        val request = RetrofitInstance.api.getFolders("Bearer $token")

        if (request.isSuccessful) {
            return request.body() ?: emptyList()
        }

        return emptyList()
    }

    suspend fun getFolderChats(folderId: Int): List<ChatInfo> {
        val tokenManager = TokenManager()
        val token = tokenManager.getToken()
        val request = RetrofitInstance.api.getFolderChats("Bearer $token",folderId)

        if (request.isSuccessful) {
            return request.body() ?: emptyList()
        }

        return emptyList()
    }

    suspend fun saveFolder(folder: ChatFolder): Boolean {
        val tokenManager = TokenManager()
        val token = tokenManager.getToken()
        val request = RetrofitInstance.api.saveFolder(token,folder)
        return request.isSuccessful
    }

    suspend fun removeFolder(folderId: Int): Boolean {
        val tokenManager = TokenManager()
        val token = tokenManager.getToken()
        val request = RetrofitInstance.api.deleteFolder(token,folderId)
        return request.isSuccessful
    }
}
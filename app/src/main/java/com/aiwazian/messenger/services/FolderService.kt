package com.aiwazian.messenger.services

import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.ChatFolder
import com.aiwazian.messenger.data.ChatInfo

class FolderService {

    suspend fun getFolders(): List<ChatFolder> {
        val request = RetrofitInstance.api.getFolders()

        if (request.isSuccessful) {
            return request.body() ?: emptyList()
        }

        return emptyList()
    }

    suspend fun getFolderChats(folderId: Int): List<ChatInfo> {
        val request = RetrofitInstance.api.getFolderChats(folderId)

        if (request.isSuccessful) {
            return request.body() ?: emptyList()
        }

        return emptyList()
    }

    suspend fun saveFolder(folder: ChatFolder): Boolean {
        val request = RetrofitInstance.api.saveFolder(folder)
        return request.isSuccessful
    }

    suspend fun removeFolder(folderId: Int): Boolean {
        val request = RetrofitInstance.api.deleteFolder(folderId)
        return request.isSuccessful
    }
}
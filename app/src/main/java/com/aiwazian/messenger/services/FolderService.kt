package com.aiwazian.messenger.services

import android.util.Log
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.ChatFolder
import com.aiwazian.messenger.data.ChatInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderService @Inject constructor() {
    
    private val _folders = MutableStateFlow<List<ChatFolder>>(emptyList())
    val folders = _folders.asStateFlow()
    
    suspend fun loadFolders() {
        try {
            val request = RetrofitInstance.api.getFolders()
            
            if (request.isSuccessful) {
                _folders.value = listOf(
                    ChatFolder(
                        id = 0,
                        folderName = "Все чаты"
                    )
                )
                _folders.value += request.body() ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("FolderService", "Error while loading folders", e)
        }
    }
    
    fun getFolderChats(folderId: Int): List<ChatInfo> {
        val folder = _folders.value.find { it.id == folderId }
        
        return folder?.chats ?: emptyList()
    }
    
    suspend fun saveFolder(folder: ChatFolder): Int? {
        val request = RetrofitInstance.api.saveFolder(folder)
        
        if (!request.isSuccessful) {
            return null
        }
        
        val savedFolderId = request.body()?.message?.toInt()
        
        if (savedFolderId == null) {
            return null
        }
        
        folder.id = savedFolderId
        
        _folders.update { currentFolders ->
            val existingIndex = currentFolders.indexOfFirst { it.id == folder.id }
            
            if (existingIndex != -1) {
                currentFolders.toMutableList().apply {
                    this[existingIndex] = folder
                }
            } else {
                currentFolders + folder
            }
        }
        
        return savedFolderId
    }
    
    suspend fun removeFolder(folderId: Int): Boolean {
        val request = RetrofitInstance.api.deleteFolder(folderId)
        
        if (request.isSuccessful) {
            val folder = _folders.value.find { it.id == folderId }
            
            if (folder != null) {
                _folders.value -= folder
            }
        }
        
        return request.isSuccessful
    }
}
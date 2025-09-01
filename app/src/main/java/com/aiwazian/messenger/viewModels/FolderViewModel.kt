package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.data.ChatFolder
import com.aiwazian.messenger.data.ChatInfo
import com.aiwazian.messenger.services.DialogController
import com.aiwazian.messenger.services.FolderService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(private val folderService: FolderService) : ViewModel() {
    
    private val _folders = MutableStateFlow<List<ChatFolder>>(emptyList())
    val folders = _folders.asStateFlow()
    
    private val _openFolder = MutableStateFlow(ChatFolder())
    val openFolder = _openFolder.asStateFlow()
    
    private val _canSave = MutableStateFlow(false)
    val canSave = _canSave.asStateFlow()
    
    val removeFolderDialog = DialogController()
    
    init {
        viewModelScope.launch {
            folderService.folders.collectLatest { folders ->
                _folders.value = folders
            }
        }
    }
    
    fun loadFolder(folderId: Int) {
        _openFolder.value = _openFolder.value.copy(
            id = folderId
        )
        
        if (folderId == 0) {
            cleanData()
            updateCanSaveState()
            return
        }
        
        val folderList = _folders.value.toMutableList()
        val folder = folderList.find { it.id == folderId }
        
        if (folder == null) {
            return
        }
        
        try {
            val folderChats = folderService.getFolderChats(folderId)
            
            _openFolder.value = _openFolder.value.copy(
                folderName = folder.folderName, chats = folderChats
            )
        } catch (e: Exception) {
            Log.e(
                "FolderViewModel", "Error loading folder chats", e
            )
        }
    }
    
    fun changeFolderName(newName: String) {
        _openFolder.value = _openFolder.value.copy(folderName = newName)
        updateCanSaveState()
    }
    
    suspend fun removeFolder(folderId: Int) {
        try {
            val isDeleted = folderService.removeFolder(folderId)
            
            if (isDeleted) {
                _folders.value = _folders.value.filter { it.id != folderId }
                cleanData()
                updateCanSaveState()
            }
        } catch (e: Exception) {
            Log.e(
                "FolderViewModel", "Error removing folder", e
            )
        }
    }
    
    fun updateFolderChats(newChats: List<ChatInfo>) {
        _openFolder.value = _openFolder.value.copy(chats = newChats)
        updateCanSaveState()
    }
    
    suspend fun saveFolder() {
        try {
            val folder = _openFolder.value
            
            folderService.saveFolder(folder)
        } catch (e: Exception) {
            Log.e(
                "FolderViewModel", "Error creating folder", e
            )
        }
    }

    private fun cleanData() {
        _openFolder.value = ChatFolder()
    }
    
    private fun updateCanSaveState() {
        _canSave.value = _openFolder.value.folderName.isNotBlank()
    }
}
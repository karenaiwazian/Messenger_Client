package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.data.ChatFolder
import com.aiwazian.messenger.data.ChatInfo
import com.aiwazian.messenger.services.FolderService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FolderViewModel : ViewModel() {
    private val folderService = FolderService()

    private val _folders = MutableStateFlow<List<ChatFolder>>(emptyList())
    val folders = _folders.asStateFlow()

    private val _folderName = MutableStateFlow("")
    val folderName = _folderName.asStateFlow()

    private val _folderChats = MutableStateFlow<List<ChatInfo>>(emptyList())
    val folderChats = _folderChats.asStateFlow()

    private val _canSave = MutableStateFlow(false)
    val canSave = _canSave.asStateFlow()

    private val _folderId = MutableStateFlow(0)

    private val _isDialogVisible = MutableStateFlow(false)
    val idDialogVisible = _isDialogVisible.asStateFlow()

    suspend fun loadFolders() {
        try {
            _folders.value = folderService.getFolders()
            _folders.value.forEachIndexed { index, folder ->
                _folders.value[index].chats = folderService.getFolderChats(folder.id)
            }
        } catch (e: Exception) {
            Log.e("FolderViewModel", "Error loading folders", e)
        }
    }

    suspend fun loadFolder(folderId: Int) {
        _folderId.value = folderId

        if (folderId == 0) {
            cleanData()
        }

        val folderList = _folders.value.toMutableList()
        val folder = folderList.find { it.id == folderId }

        if (folder == null) {
            return
        }

        _folderName.value = folder.folderName
        try {
            _folderChats.value = folderService.getFolderChats(folderId)
        } catch (e: Exception) {
            Log.e("FolderViewModel", "Error loading folder chats", e)
        }
    }

    fun changeFolderName(newName: String) {
        _folderName.value = newName
        updateCanSaveState()
    }

    suspend fun removeFolder(folderId: Int) {
        try {
            val isDeleted = folderService.removeFolder(folderId)

            if (isDeleted) {
                _folders.value = _folders.value.filter { it.id != folderId }
                cleanData()
            }
        } catch (e: Exception) {
            Log.e("FolderViewModel", "Error removing folder", e)
        }
    }

    fun updateChatsForFolder(newChats: List<ChatInfo>) {
        _folderChats.value = newChats
        updateCanSaveState()
    }

    suspend fun saveFolder() {
        val folder = ChatFolder(
            id = _folderId.value,
            folderName = _folderName.value,
            chats = _folderChats.value
        )

        try {
            folderService.saveFolder(folder)

            val folderList = _folders.value.toMutableList()
            val changedFolder = folderList.find { it.id == folder.id }
            if (changedFolder == null) {
                _folders.value += folder
            } else {
                folderList.remove(changedFolder)
                folderList.add(folder)
                _folders.value = folderList
            }
        } catch (e: Exception) {
            Log.e("FolderViewModel", "Error creating folder", e)
        }
    }

    fun showDialog() {
        _isDialogVisible.value = true
    }

    fun hideDialog() {
        _isDialogVisible.value = false
    }

    fun cleanData() {
        _folderId.value = 0
        _folderName.value = ""
        _folderChats.value = emptyList()
    }

    private fun updateCanSaveState() {
        _canSave.value = _folderName.value.isNotBlank()
    }
}
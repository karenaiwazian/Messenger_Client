package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.ChatInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatsViewModel : ViewModel() {
    private val _unarchivedChats = MutableStateFlow<List<ChatInfo>>(emptyList())
    val unarchivedChats = _unarchivedChats.asStateFlow()

    private val _archivedChats = MutableStateFlow<List<ChatInfo>>(emptyList())
    val archivedChats = _archivedChats.asStateFlow()

    private val _selectedChats = MutableStateFlow<List<Int>>(emptyList())
    val selectedChats = _selectedChats.asStateFlow()

    private val _allSelectedArePinned = MutableStateFlow(false)
    val allSelectedArePinned = _allSelectedArePinned.asStateFlow()

    init {
        viewModelScope.launch {
            _selectedChats.combine(_unarchivedChats) { selectedIds, unarchivedInfos ->
                if (selectedIds.isEmpty()) {
                    false
                } else {
                    selectedIds.all { selectedChatId ->
                        unarchivedInfos.find { it.id == selectedChatId }?.isPinned == true
                    }
                }
            }.collect { allArePinned ->
                _allSelectedArePinned.value = allArePinned
            }
        }
    }

    suspend fun loadUnarchiveChats() {
        try {
            val response = RetrofitInstance.api.getUnarchivedChats()

            val responseBody = response.body()

            if (!response.isSuccessful || responseBody == null) {
                return
            }

            _unarchivedChats.value = responseBody
        } catch (e: Exception) {
            Log.e("ChatsViewModel", "Error loading unarchive chats", e)
        }
    }

    suspend fun loadArchiveChats() {
        try {
            val response = RetrofitInstance.api.getArchivedChats()

            val responseBody = response.body()

            if (!response.isSuccessful || responseBody == null) {
                return
            }

            _archivedChats.value = responseBody
        } catch (e: Exception) {
            Log.e("ChatsViewModel", "Error loading unarchive chats", e)
        }
    }

    fun selectChat(chatId: Int) {
        val currentList = _selectedChats.value.toMutableList()

        if (currentList.contains(chatId)) {
            currentList.remove(chatId)
        } else {
            currentList.add(chatId)
        }

        _selectedChats.value = currentList
    }

    fun unselectAllChats() {
        _selectedChats.value = emptyList()
    }

    suspend fun pinChat(chatId: Int) {
        val currentList = _unarchivedChats.value.toMutableList()
        val chatToPin = currentList.find { it.id == chatId }

        if (chatToPin == null) {
            return
        }

        chatToPin.isPinned = true

        currentList.remove(chatToPin)
        currentList.add(0, chatToPin)

        _unarchivedChats.value = currentList.toList()

        try {
            val chat = ChatInfo(chatId)
            RetrofitInstance.api.pinChat(chat)
        } catch (e: Exception) {
            Log.e("ChatsViewModel", "Error while pin chat " + e.message)
        }
    }

    suspend fun unpinChat(chatId: Int) {
        val currentList = _unarchivedChats.value.toMutableList()
        val selectedChatInfo = currentList.find { it.id == chatId }

        if (selectedChatInfo == null) {
            return
        }

        selectedChatInfo.isPinned = false

        currentList.remove(selectedChatInfo)
        currentList.add(_unarchivedChats.value.count { it.isPinned }, selectedChatInfo)

        _unarchivedChats.value = currentList.toList()

        try {
            val chat = ChatInfo(chatId)
            RetrofitInstance.api.unpinChat(chat)
        } catch (e: Exception) {
            Log.e("ChatsViewModel", "Error while unpin chat " + e.message)
        }
    }

    suspend fun archiveChat(chatId: Int) {
        var chatToArchive: ChatInfo? = null

        _unarchivedChats.update { currentUnarchived ->
            val mutableList = currentUnarchived.toMutableList()
            val index = mutableList.indexOfFirst { it.id == chatId }

            if (index != -1) {
                chatToArchive = mutableList.removeAt(index)
            } else {
                Log.w("ChatsViewModel", "Chat to archive not found in unarchived list: $chatId")
                return@update currentUnarchived
            }
            mutableList
        }

        if (chatToArchive == null) {
            return
        }

        val finalChatToArchive = chatToArchive.copy(isPinned = false)

        _archivedChats.update { currentArchived ->
            val mutableList = currentArchived.toMutableList()
            mutableList.removeAll { it.id == finalChatToArchive.id }
            mutableList.add(finalChatToArchive)
            mutableList
        }

        val requestBody = ChatInfo(id = chatId)

        try {
            RetrofitInstance.api.archiveChat(requestBody)
        } catch (e: Exception) {
            Log.e("ChatsViewModel", "Error while adding chat to archive: " + e.message)
        }
    }

    suspend fun unarchiveChat(chatId: Int) {
        var chatToUnarchive: ChatInfo? = null

        _archivedChats.update { currentArchived ->
            val mutableList = currentArchived.toMutableList()
            val index = mutableList.indexOfFirst { it.id == chatId }

            if (index != -1) {
                chatToUnarchive = mutableList.removeAt(index)
            } else {
                Log.w("ChatsViewModel", "Chat to unarchive not found in archived list: $chatId")
                return@update currentArchived
            }
            mutableList
        }

        if (chatToUnarchive == null) {
            return
        }

        val finalChatToUnarchive = chatToUnarchive.copy(isPinned = false)

        _unarchivedChats.update { currentUnarchived ->
            val mutableList = currentUnarchived.toMutableList()
            mutableList.removeAll { it.id == finalChatToUnarchive.id }
            mutableList.add(finalChatToUnarchive)
            mutableList
        }

        val requestBody = ChatInfo(id = chatId)

        try {
            RetrofitInstance.api.unarchiveChat(requestBody)
        } catch (e: Exception) {
            Log.e("ChatsViewModel", "Error while deleting chat from archive: " + e.message)
        }
    }

    fun moveToUp(chatId: Int) {
        val currentList = _unarchivedChats.value.toMutableList()
        val chat = currentList.find { it.id == chatId }

        if (chat == null) {
            return
        }

        currentList.remove(chat)
        currentList.add(0, chat)

        _unarchivedChats.value = currentList
    }
}

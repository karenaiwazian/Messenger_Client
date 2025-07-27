package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.ChatInfo
import com.aiwazian.messenger.utils.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

class ChatsViewModel : ViewModel() {
    private val _unarchivedChats = MutableStateFlow<List<ChatInfo>>(emptyList())
    val unarchivedChats = _unarchivedChats.asStateFlow()

    private val _archivedChats = MutableStateFlow<List<ChatInfo>>(emptyList())
    val archivedChats = _archivedChats.asStateFlow()

    private val _selectedChats = MutableStateFlow<List<Int>>(emptyList())
    val selectedChats = _selectedChats.asStateFlow()

    private val dataStore = DataStoreManager.getInstance()

    suspend fun loadUnarchiveChats() {
        try {
            val token = dataStore.getToken().first()
            val response = RetrofitInstance.api.getUnarchivedChats("Bearer $token")

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
            val token = dataStore.getToken().first()
            val response = RetrofitInstance.api.getArchivedChats("Bearer $token")

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
        val index = currentList.indexOfFirst { it.id == chatId }

        if (index == -1) {
            return
        }

        val selectedChatInfo = currentList.first { it.id == chatId }
        currentList.remove(selectedChatInfo)
        currentList.add(0, selectedChatInfo)

        _unarchivedChats.value = currentList

        val token = dataStore.getToken().first()
        try {
            RetrofitInstance.api.pinChat("Bearer $token", ChatInfo(id = chatId))
        } catch(e: Exception) {
            Log.e("ChatsViewModel", "Error while pin chat " + e.message)
        }
    }

    suspend fun unpinChat(chatId: Int) {
        val currentList = _unarchivedChats.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == chatId }

        if (index == -1) {
            return
        }

        val item = currentList.removeAt(index)
        _unarchivedChats.value += item

        val token = dataStore.getToken().first()
        RetrofitInstance.api.unpinChat("Bearer $token", ChatInfo(id = chatId))
    }

    suspend fun archiveChat(chatId: Int) {
        val currentList = _unarchivedChats.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == chatId }

        if (index == -1) {
            return
        }

        val item = currentList.removeAt(index)
        _archivedChats.value += item
        _unarchivedChats.value -= item

        val token = dataStore.getToken().first()
        val chatInfo = ChatInfo(id = chatId)
        try {
            RetrofitInstance.api.archiveChat(token = "Bearer $token", chatInfo)
        } catch (e: Exception) {
            Log.e("ChatsViewModel", "Error while ad chat to archive " + e.message)
        }
    }

    suspend fun unarchiveChat(chatId: Int) {
        val currentList = _archivedChats.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == chatId }

        if (index == -1) {
            return
        }

        val item = currentList.removeAt(index)
        _unarchivedChats.value += item
        _archivedChats.value -= item

        val token = dataStore.getToken().first()
        val chatInfo = ChatInfo(id = chatId)
        try {
            RetrofitInstance.api.unarchiveChat(token = "Bearer $token", chatInfo)
        } catch (e: Exception) {
            Log.e("ChatsViewModel", "Error while delete chat from archive")
        }
    }

    fun moveToUp(chatId: Int) {
        val currentList = _unarchivedChats.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == chatId }

        if (index == -1) {
            return
        }

        val item = currentList.removeAt(index)
        currentList.add(0, item)
        _unarchivedChats.value = currentList
    }
}

package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.customType.WebSocketAction
import com.aiwazian.messenger.data.ChatFolder
import com.aiwazian.messenger.data.ChatInfo
import com.aiwazian.messenger.data.DeleteMessagePayload
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.data.ReadMessagePayload
import com.aiwazian.messenger.services.ChatService
import com.aiwazian.messenger.services.FolderService
import com.aiwazian.messenger.utils.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val chatService: ChatService,
    private val folderService: FolderService
) : ViewModel() {
    
    private val _unarchivedChats = MutableStateFlow<List<ChatInfo>>(emptyList())
    
    private val _archivedChats = MutableStateFlow<List<ChatInfo>>(emptyList())
    val archivedChats = _archivedChats.asStateFlow()
    
    /** Folder id - Chat id **/
    private val _selectedChats = MutableStateFlow<Map<Int, List<Int>>>(emptyMap())
    val selectedChats = _selectedChats.asStateFlow()
    
    private val _chatFolders = MutableStateFlow<List<ChatFolder>>(emptyList())
    val chatFolders = _chatFolders.asStateFlow()
    
    private val _allSelectedArePinned = MutableStateFlow(false)
    val allSelectedArePinned = _allSelectedArePinned.asStateFlow()
    
    init {
        WebSocketManager.registerMessageHandler<Message>(WebSocketAction.NEW_MESSAGE) { message ->
            onReceivingMessage(message)
        }
        
        WebSocketManager.registerMessageHandler<DeleteMessagePayload>(WebSocketAction.DELETE_MESSAGE) { message ->
            onMessageDeleted(
                message.messageId,
                message.chatId
            )
        }
        
        WebSocketManager.registerMessageHandler<ReadMessagePayload>(WebSocketAction.READ_MESSAGE) { message ->
            onReadMessage(
                message.chatId,
                message.messageId
            )
        }
        
        viewModelScope.launch {
            tryLoadUnarchiveChats()
            
            tryLoadArchiveChats()
            
            folderService.loadFolders()
            
            folderService.folders.collectLatest { folders ->
                folders.find { it.id == 0 }?.chats = _unarchivedChats.value
                
                _chatFolders.update { folders }
            }
        }
    }
    
    fun onSendMessage(message: Message) {
        processMessage(
            message.chatId,
            message
        )
    }
    
    fun onReceivingMessage(message: Message) {
        processMessage(
            message.senderId,
            message
        )
    }
    
    private fun onReadMessage(
        chatId: Int,
        messageId: Int
    ) {
        _chatFolders.update { currentFolders ->
            currentFolders.map { folder ->
                val chatToUpdate = folder.chats.find { it.id == chatId }
                
                if (chatToUpdate == null || chatToUpdate.lastMessage?.id != messageId) {
                    return@map folder
                }
                
                try {
                    if (chatToUpdate.lastMessage == null) {
                        return
                    }

                    val lastMessage = chatToUpdate.lastMessage!!.copy(isRead = true)
                    
                    val updatedChat = chatToUpdate.copy(lastMessage = lastMessage)
                    
                    val updatedChats = folder.chats.map {
                        if (it.id == chatId) updatedChat else it
                    }
                    
                    folder.copy(chats = updatedChats)
                } catch (e: Exception) {
                    Log.e(
                        "MainScreenViewModel",
                        "Error updating last message after deletion",
                        e
                    )
                    folder
                }
            }
        }
    }
    
    private fun onMessageDeleted(
        messageId: Int,
        chatId: Int
    ) {
        viewModelScope.launch {
            _chatFolders.update { currentFolders ->
                currentFolders.map { folder ->
                    val chatToUpdate = folder.chats.find { it.id == chatId }
                    
                    if (chatToUpdate == null || chatToUpdate.lastMessage?.id != messageId) {
                        return@map folder
                    }
                    
                    try {
                        val lastMessage = chatService.getChatLastMessage(chatId)
                        
                        val updatedChat = chatToUpdate.copy(lastMessage = lastMessage)
                        
                        val updatedChats = folder.chats.map {
                            if (it.id == chatId) updatedChat else it
                        }
                        
                        folder.copy(chats = updatedChats)
                    } catch (e: Exception) {
                        Log.e(
                            "MainScreenViewModel",
                            "Error updating last message after deletion",
                            e
                        )
                        folder
                    }
                }
            }
        }
    }
    
    private fun processMessage(
        chatId: Int,
        message: Message
    ) {
        var chatFound = false
        
        _chatFolders.value.forEach { folder ->
            if (folder.chats.any { it.id == chatId }) {
                updateLastMessage(
                    chatId,
                    message
                )
                chatFound = true
                return@forEach
            }
        }
        
        if (!chatFound) {
            viewModelScope.launch {
                val chatInfo = chatService.getChatInfo(chatId)
                
                if (chatInfo != null) {
                    showNewChat(
                        chatInfo,
                        message
                    )
                }
            }
        }
    }
    
    fun selectChat(
        chatId: Int,
        folderId: Int
    ) {
        _selectedChats.update { currentMap ->
            val existingFolders = currentMap[folderId] ?: emptyList()
            
            if (chatId !in existingFolders) {
                val updatedFolders = existingFolders + chatId
                currentMap + (folderId to updatedFolders)
            } else {
                val updatedFolders = existingFolders - chatId
                
                if (updatedFolders.isEmpty()) {
                    currentMap - folderId
                } else {
                    currentMap + (folderId to updatedFolders)
                }
            }
        }
        
        _allSelectedArePinned.update { true }
        
        _selectedChats.value.forEach { folder ->
            val selectedChatFolder = _chatFolders.value.find { it.id == folder.key }
            
            if (selectedChatFolder == null) {
                return@forEach
            }
            
            folder.value.forEach { selectedChatId ->
                val selectedChat = selectedChatFolder.chats.find { it.id == selectedChatId }
                
                if (selectedChat == null) {
                    return@forEach
                }
                
                if (!selectedChat.isPinned) {
                    _allSelectedArePinned.update { false }
                    return
                }
            }
        }
    }
    
    fun unselectAllChats() {
        _selectedChats.update { emptyMap() }
    }
    
    suspend fun pinChat(
        chatId: Int,
        folderId: Int
    ) {
        val folder = _chatFolders.value.find { it.id == folderId }
        val chatToPin = folder?.chats?.find { it.id == chatId }
        
        if (chatToPin == null || chatToPin.isPinned) {
            return
        }
        
        _chatFolders.update { currentFolderList ->
            currentFolderList.map { currentFolder ->
                if (currentFolder.id == folderId) {
                    val pinnedChat = chatToPin.copy(isPinned = true)
                    
                    val newChatList = currentFolder.chats.filter { it.id != chatId }
                        .toMutableList()
                    
                    newChatList.add(pinnedChat)
                    
                    val pinnedChats = newChatList.filter { it.isPinned }
                        .toMutableList()
                    val unpinnedChats = newChatList.filter { !it.isPinned }
                    
                    pinnedChats.sortByDescending { it.lastMessage?.sendTime }
                    
                    val finalChats = pinnedChats + unpinnedChats
                    
                    currentFolder.copy(chats = finalChats)
                } else {
                    currentFolder
                }
            }
        }
        
        try {
            chatService.pinChat(
                chatId,
                folderId
            )
        } catch (e: Exception) {
            Log.e(
                "ChatsViewModel",
                "Error while pin chat " + e.message
            )
        }
    }
    
    suspend fun unpinChat(
        chatId: Int,
        folderId: Int
    ) {
        val folder = _chatFolders.value.find { it.id == folderId }
        val chatToUnpin = folder?.chats?.find { it.id == chatId }
        
        if (chatToUnpin == null || !chatToUnpin.isPinned) {
            return
        }
        
        _chatFolders.update { currentFolderList ->
            currentFolderList.map { currentFolder ->
                if (currentFolder.id == folderId) {
                    val updatedChats = currentFolder.chats.toMutableList()
                    
                    updatedChats.remove(chatToUnpin)
                    
                    val unpinnedChat = chatToUnpin.copy(isPinned = false)
                    
                    updatedChats.add(unpinnedChat)
                    
                    val pinnedChats = updatedChats.filter { it.isPinned }
                    val unpinnedChats = updatedChats.filter { !it.isPinned }
                    
                    val sortedUnpinnedChats =
                        unpinnedChats.sortedByDescending { it.lastMessage?.sendTime }
                    
                    val finalChats = pinnedChats + sortedUnpinnedChats
                    
                    currentFolder.copy(chats = finalChats)
                } else {
                    currentFolder
                }
            }
        }
        
        try {
            chatService.unpinChat(
                chatId,
                folderId
            )
        } catch (e: Exception) {
            Log.e(
                "ChatsViewModel",
                "Error while unpin chat " + e.message
            )
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
                return@update currentUnarchived
            }
            
            mutableList
        }
        
        if (chatToArchive == null) {
            return
        }
        
        _archivedChats.update { currentArchived ->
            (currentArchived + chatToArchive).distinctBy { it.id }
        }
        
        _chatFolders.update { currentFolders ->
            val updatedFolders = currentFolders.map { folder ->
                if (folder.id == 0) {
                    folder.copy(chats = _unarchivedChats.value)
                } else {
                    folder
                }
            }
            
            updatedFolders
        }
        
        try {
            chatService.archiveChat(chatId)
        } catch (e: Exception) {
            Log.e(
                "ChatsViewModel",
                "Error while adding chat to archive: " + e.message
            )
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
                return@update currentArchived
            }
            
            mutableList
        }
        
        if (chatToUnarchive == null) {
            return
        }
        
        showNewChat(
            chatToUnarchive,
            chatToUnarchive.lastMessage ?: Message()
        )
        
        try {
            chatService.unarchiveChat(chatId)
        } catch (e: Exception) {
            Log.e(
                "ChatsViewModel",
                "Error while deleting chat from archive: " + e.message
            )
        }
    }
    
    fun addChatToFolder(
        chatId: Int,
        folderId: Int
    ) { // TODO добавление чата в папку
    }
    
    fun removeChatFromFolder(
        chatId: Int,
        folderId: Int
    ) { // TODO удаление чата из папки
    }
    
    private fun updateLastMessage(
        chatId: Int,
        lastMessage: Message
    ) {
        _chatFolders.update { currentFolders ->
            currentFolders.map { folder ->
                val chatToUpdate = folder.chats.find { it.id == chatId }
                
                if (chatToUpdate == null) {
                    return@map folder
                }
                
                val updatedChat = chatToUpdate.copy(lastMessage = lastMessage)
                
                val otherChats = folder.chats.filter { it.id != chatId }
                
                val pinnedChats =
                    (otherChats.filter { it.isPinned } + (if (updatedChat.isPinned) updatedChat else null)).filterNotNull()
                        .sortedByDescending { it.lastMessage?.sendTime }
                
                val unpinnedChats =
                    (otherChats.filter { !it.isPinned } + (if (!updatedChat.isPinned) updatedChat else null)).filterNotNull()
                        .sortedByDescending { it.lastMessage?.sendTime }
                
                val finalChats = pinnedChats + unpinnedChats
                
                folder.copy(chats = finalChats)
            }
        }
    }
    
    private fun showNewChat(
        chatInfo: ChatInfo,
        lastMessage: Message
    ) {
        val newChatInfo = chatInfo.copy(lastMessage = lastMessage)
        
        _unarchivedChats.update { currentChats ->
            val pinnedChats = currentChats.filter { it.isPinned }
            val unpinnedChats = currentChats.filter { !it.isPinned }
            
            val updatedUnpinnedChats = unpinnedChats.toMutableList()
            updatedUnpinnedChats.add(newChatInfo)
            
            updatedUnpinnedChats.sortByDescending { it.lastMessage?.sendTime }
            
            pinnedChats + updatedUnpinnedChats
        }
        
        _chatFolders.update { currentFolders ->
            currentFolders.map { folder ->
                if (folder.id == 0) {
                    val pinnedChats = folder.chats.filter { it.isPinned }
                    val unpinnedChats = folder.chats.filter { !it.isPinned }
                    
                    val updatedUnpinnedChats = unpinnedChats.toMutableList()
                    updatedUnpinnedChats.add(newChatInfo)
                    
                    updatedUnpinnedChats.sortByDescending { it.lastMessage?.sendTime }
                    
                    val updatedChats = pinnedChats + updatedUnpinnedChats
                    folder.copy(chats = updatedChats)
                } else {
                    folder
                }
            }
        }
    }
    
    private suspend fun tryLoadUnarchiveChats() {
        try {
            val response = RetrofitInstance.api.getUnarchivedChats()
            
            val responseBody = response.body()
            
            if (!response.isSuccessful || responseBody == null) {
                return
            }
            
            _unarchivedChats.update { responseBody }
        } catch (e: Exception) {
            Log.e(
                "ChatsViewModel",
                "Error loading unarchive chats",
                e
            )
        }
    }
    
    private suspend fun tryLoadArchiveChats() {
        try {
            val response = RetrofitInstance.api.getArchivedChats()
            
            val responseBody = response.body()
            
            if (!response.isSuccessful || responseBody == null) {
                return
            }
            
            _archivedChats.update { responseBody }
        } catch (e: Exception) {
            Log.e(
                "ChatsViewModel",
                "Error loading unarchive chats",
                e
            )
        }
    }
}

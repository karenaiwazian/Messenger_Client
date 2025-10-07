package com.aiwazian.messenger.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("folderChat")
data class FolderChatEntity(
    @PrimaryKey var id: Int,
    val folderId: Int = 0,
    val chatType: Int,
    var chatName: String = "",
    var isPinned: Boolean = false,
    var lastMessageId: Int? = null
)
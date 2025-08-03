package com.aiwazian.messenger.data

import androidx.annotation.Keep

@Keep
data class ChatFolder(
    @Keep val id: Int = 0,
    @Keep val folderName: String = "",
    @Keep val userId: Int = 0,
    @Keep var chats: List<ChatInfo> = emptyList()
)

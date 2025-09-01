package com.aiwazian.messenger.data

import androidx.annotation.Keep

@Keep
data class ChatFolder(
    @Keep var id: Int = 0,
    @Keep var folderName: String = "",
    @Keep val userId: Int = 0,
    @Keep var chats: List<ChatInfo> = emptyList()
)

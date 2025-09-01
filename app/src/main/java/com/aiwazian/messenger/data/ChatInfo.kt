package com.aiwazian.messenger.data

import androidx.annotation.Keep

@Keep
data class ChatInfo(
    @Keep var id: Int = 0,
    @Keep val chatName: String = "",
    @Keep var isPinned: Boolean = false,
    @Keep var lastMessage: Message? = null
)

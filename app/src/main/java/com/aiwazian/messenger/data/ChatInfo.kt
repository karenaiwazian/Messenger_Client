package com.aiwazian.messenger.data

import androidx.annotation.Keep
import com.aiwazian.messenger.enums.ChatType

@Keep
data class ChatInfo(
    @Keep var id: Int = 0,
    @Keep var chatName: String = "",
    @Keep var isPinned: Boolean = false,
    @Keep var chatType: ChatType = ChatType.UNKNOWN,
    @Keep var lastMessage: Message? = null
)

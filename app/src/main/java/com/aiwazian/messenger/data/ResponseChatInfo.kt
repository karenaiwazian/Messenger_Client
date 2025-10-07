package com.aiwazian.messenger.data

import androidx.annotation.Keep
import com.aiwazian.messenger.enums.ChatType

@Keep
data class ResponseChatInfo(
    @Keep var id: Int = 0,
    @Keep var chatName: String = "",
    @Keep var isPinned: Boolean = false,
    @Keep var chatType: Int = ChatType.UNKNOWN.ordinal,
    @Keep var lastMessage: Message? = null
) {
    fun toChatInto(): ChatInfo {
        return ChatInfo(
            id = this.id,
            chatName = this.chatName,
            isPinned = this.isPinned,
            chatType = ChatType.fromInt(this.chatType),
            lastMessage = this.lastMessage,
        )
    }
}

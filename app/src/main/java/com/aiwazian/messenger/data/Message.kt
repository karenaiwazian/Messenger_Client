package com.aiwazian.messenger.data

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Message(
    @Keep var id: Int = 0,
    @Keep val senderId: Int = 0,
    @Keep val chatId: Int = 0,
    @Keep val text: String = "",
    @Keep val sendTime: Long = 0,
    @Keep var isRead: Boolean = false
)

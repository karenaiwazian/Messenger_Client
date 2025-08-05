package com.aiwazian.messenger.data

import androidx.annotation.Keep
import java.time.Instant

@Keep
data class Message(
    @Keep val id: Int = 0,
    @Keep val senderId: Int = 0,
    @Keep val chatId: Int = 0,
    @Keep val messageId: Int = 0,
    @Keep val text: String = "",
    @Keep val sendTime: Long = 0
)

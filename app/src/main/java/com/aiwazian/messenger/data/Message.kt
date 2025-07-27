package com.aiwazian.messenger.data

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Message(
    @Keep val id: Int = 0,
    @Keep val senderId: Int = 0,
    @Keep val receiverId: Int = 0,
    @Keep val text: String = "",
    @Keep val timestamp: Long = System.currentTimeMillis(),
) : WebSocketData

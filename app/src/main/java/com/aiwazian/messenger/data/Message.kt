package com.aiwazian.messenger.data

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Message(
    @Keep val id: String = "",
    @Keep val senderId: String = "",
    @Keep val receiverId: String = "",
    @Keep val text: String = "",
    @Keep val timestamp: Long = System.currentTimeMillis(),
) : WebSocketData

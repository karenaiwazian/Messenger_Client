package com.aiwazian.messenger.data

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class WebSocketMessage(
    @Keep val action: String,
    @Keep val data: MessageWrapper
)

@Keep
@Serializable
data class MessageWrapper(
    @Keep val message: WebSocketMessageContent
)

@Keep
@Serializable
data class WebSocketMessageContent(
    @Keep val id: Int = 0,
    @Keep val messageId: Int = 0,
    @Keep val senderId: Int = 0,
    @Keep val chatId: Int = 0,
    @Keep val text: String = "",
    @Keep val sendTime: Long = 0,
)
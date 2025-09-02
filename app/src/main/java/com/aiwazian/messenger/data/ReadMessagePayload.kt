package com.aiwazian.messenger.data

import kotlinx.serialization.Serializable

@Serializable
data class ReadMessagePayload(
    val chatId: Int,
    val messageId: Int
)
package com.aiwazian.messenger.data

import kotlinx.serialization.Serializable

@Serializable
data class DeleteMessagePayload(
    val chatId: Int,
    val messageId: Int
)
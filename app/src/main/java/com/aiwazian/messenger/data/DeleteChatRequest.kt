package com.aiwazian.messenger.data

import androidx.annotation.Keep

@Keep
data class DeleteChatRequest(
    @Keep val chatId: String,
    @Keep val deletedBySender: Boolean,
    @Keep val deletedByReceiver: Boolean
)
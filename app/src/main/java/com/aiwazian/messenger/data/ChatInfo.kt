package com.aiwazian.messenger.data

import androidx.annotation.Keep

@Keep
data class ChatInfo(
    @Keep val id: Int,
    @Keep val chatName: String = "",
    @Keep val isPinned: Boolean = false
)

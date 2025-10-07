package com.aiwazian.messenger.data

import androidx.annotation.Keep

@Keep
data class SearchInfo(
    @Keep val chatId: Int,
    @Keep val name: String,
    @Keep val publicLink: String
)

package com.aiwazian.messenger.data

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Session(
    @Keep val id: String,
    @Keep val deviceName: String,
    @Keep val createdAt: String,
)

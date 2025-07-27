package com.aiwazian.messenger.data

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class DismissSession(
    @Keep val sessionId: Int,
) : WebSocketData

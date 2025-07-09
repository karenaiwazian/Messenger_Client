package com.aiwazian.messenger.data

import androidx.annotation.Keep
import com.aiwazian.messenger.customType.WebSocketAction
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class WebSocketMessage (
    @Keep val action: WebSocketAction,
    @Keep val data: WebSocketData
)
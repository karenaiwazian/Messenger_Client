package com.aiwazian.messenger.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class WebSocketAction {
    @SerialName("NEW_MESSAGE") NEW_MESSAGE,
    
    @SerialName("UPDATE_MESSAGE") UPDATE_MESSAGE,
    
    @SerialName("DELETE_MESSAGE") DELETE_MESSAGE,
    
    @SerialName("DELETE_CHAT") DELETE_CHAT,
    
    @SerialName("READ_MESSAGE") READ_MESSAGE, UNKNOWN
}
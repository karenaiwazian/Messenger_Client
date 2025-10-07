package com.aiwazian.messenger.enums

enum class ChatType {
    PRIVATE,
    GROUP,
    CHANNEL,
    UNKNOWN;
    
    companion object {
        fun fromInt(value: Int): ChatType {
            return entries.firstOrNull { it.ordinal == value } ?: UNKNOWN
        }
    }
}
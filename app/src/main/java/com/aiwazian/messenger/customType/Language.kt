package com.aiwazian.messenger.customType

enum class Language {
    RU, EN;

    companion object {
        fun fromString(value: String): Language {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: EN
        }
    }
}
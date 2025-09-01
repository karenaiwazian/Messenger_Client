package com.aiwazian.messenger.utils

data class NotificationChannelInfo(
    val id: String,
    val name: String,
    val description: String
)

object NotificationChannelConstants {
    val PERSONAL_MESSAGES = NotificationChannelInfo(
        "personal_messages",
        "Личные сообщения",
        "Уведомления для новых сообщений в личных чатах"
    )
}
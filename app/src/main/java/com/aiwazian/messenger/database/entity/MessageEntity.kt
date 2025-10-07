package com.aiwazian.messenger.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("message")
data class MessageEntity(
    @PrimaryKey var id: Int,
    val senderId: Int,
    val chatId: Int,
    val text: String = "",
    val sendTime: Long = 0,
    var isRead: Boolean = false
)
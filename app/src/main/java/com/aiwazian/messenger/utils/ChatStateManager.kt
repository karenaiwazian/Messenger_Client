package com.aiwazian.messenger.utils

object ChatStateManager {
    private var openChatId: Int? = null

    fun openChat(chatId: Int) {
        openChatId = chatId
    }

    fun closeChat() {
        openChatId = null
    }

    fun isChatOpen(chatId: Int?): Boolean {
        return openChatId == chatId
    }
}

package com.aiwazian.messenger.utils

object ChatStateManager {
    private var openChatId: String? = null

    fun openChat(chatId: String) {
        openChatId = chatId
    }

    fun closeChat() {
        openChatId = null
    }

    fun isChatOpen(chatId: String?): Boolean {
        return openChatId == chatId
    }
}

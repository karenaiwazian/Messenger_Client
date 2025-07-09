package com.aiwazian.messenger

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class ClipboardHelper(private val context: Context) {
    fun copy(text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clipData = ClipData.newPlainText("label", text)

        clipboardManager.setPrimaryClip(clipData)
    }
}

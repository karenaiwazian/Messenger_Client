package com.aiwazian.messenger.utils

import android.util.Log
import com.aiwazian.messenger.api.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AuthManager {
    fun logout() {
        val store = DataStoreManager.Companion.getInstance()
        val token = UserManager.token

        CoroutineScope(Dispatchers.IO).launch {
            store.saveToken("")
            try {
                RetrofitInstance.api.logout("Bearer $token")
            } catch (e: Exception) {
                Log.e("AuthManager", "Ошибка при выходе: ${e.message}")
            }
        }
    }
}
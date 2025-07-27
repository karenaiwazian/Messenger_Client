package com.aiwazian.messenger.utils

import android.util.Log
import com.aiwazian.messenger.api.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthService {
    fun logout() {
        val store = DataStoreManager.Companion.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            val token = store.getToken().first()
            store.saveToken("")

            try {
                RetrofitInstance.api.logout("Bearer $token")
            } catch (e: Exception) {
                Log.e("AuthManager", "Ошибка при выходе: ${e.message}")
            }
        }
    }
}

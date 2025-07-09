package com.aiwazian.messenger

import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.AuthRequest
import com.aiwazian.messenger.data.RegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AuthManager {
    fun logout() {
        val store = DataStoreManager.getInstance()
        val token = UserManager.token
        store.removeToken()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitInstance.api.logout("Bearer $token")
            } catch (e: Exception) {

            }
        }
    }
}
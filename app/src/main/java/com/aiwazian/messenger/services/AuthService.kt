package com.aiwazian.messenger.services

import android.util.Log
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.utils.WebSocketManager

class AuthService {
    suspend fun logout() {
        try {
            RetrofitInstance.api.logout()
            WebSocketManager.close()
            TokenManager.setAuthorized(false)
            TokenManager.removeToken()
        } catch (e: Exception) {
            Log.e("AuthManager", "Ошибка при выходе: ${e.message}")
        }
    }
}
package com.aiwazian.messenger.services

import android.util.Log
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.services.TokenManager

class AuthService {
    suspend fun logout() {
        try {
            val tokenManager = TokenManager()
            val token = tokenManager.getToken()
            RetrofitInstance.api.logout("Bearer $token")
        } catch (e: Exception) {
            Log.e("AuthManager", "Ошибка при выходе: ${e.message}")
        }
    }
}
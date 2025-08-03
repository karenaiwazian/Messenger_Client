package com.aiwazian.messenger.services

import android.util.Log
import com.aiwazian.messenger.api.RetrofitInstance

class AuthService {
    suspend fun logout() {
        try {
            RetrofitInstance.api.logout()
        } catch (e: Exception) {
            Log.e("AuthManager", "Ошибка при выходе: ${e.message}")
        }
    }
}
package com.aiwazian.messenger.services

import android.util.Log
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserService {

    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    fun updateUser(updatedUser: User) {
        _user.value = updatedUser
    }

    suspend fun loadUserData() {
        try {
            val tokenManager = TokenManager()
            val token = tokenManager.getToken()
            val response = RetrofitInstance.api.getMe("Bearer $token")

            if (response.isSuccessful) {
                val responseBody = response.body()

                if (responseBody != null) {
                    updateUser(responseBody)
                }

                Log.d("UserManager", "User loaded: $user")
            }
        } catch (e: Exception) {
            Log.e("UserManager", "An unexpected error occurred while loading user", e)
        }
    }

    suspend fun saveUserData() {
        try {
            val tokenManager = TokenManager()
            val token = tokenManager.getToken()
            val response = RetrofitInstance.api.updateProfile(token,_user.value)
            if (response.isSuccessful) {
                Log.d("UserManager", "change is success $user")
            }
        } catch (e: Exception) {
            Log.e("UserManager", "Error saving user data", e)
        }
    }
}
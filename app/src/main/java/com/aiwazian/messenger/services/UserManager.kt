package com.aiwazian.messenger.services

import android.util.Log
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserManager {

    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    fun updateUserInfo(updatedUser: User) {
        val userId = _user.value.id
        _user.value = updatedUser
        _user.value.id = userId
    }

    suspend fun loadUserData() {
        try {
            val response = RetrofitInstance.api.getMe()

            if (response.isSuccessful) {
                val responseBody = response.body()

                if (responseBody != null) {
                    _user.value = responseBody
                }

                Log.d("UserManager", "User loaded: $user")
            }
        } catch (e: Exception) {
            Log.e("UserManager", "An unexpected error occurred while loading user", e)
        }
    }
}
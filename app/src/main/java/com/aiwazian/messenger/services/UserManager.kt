package com.aiwazian.messenger.services

import android.util.Log
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object UserManager {
    
    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()
    
    fun updateUserInfo(updatedUser: User) {
        val newUserInfo = _user.value.copy(
            firstName = updatedUser.firstName,
            lastName = updatedUser.lastName,
            bio = updatedUser.bio,
            username = updatedUser.username,
            dateOfBirth = updatedUser.dateOfBirth,
        )
        _user.update { newUserInfo }
    }
    
    suspend fun loadUserData() {
        try {
            val response = RetrofitInstance.api.getMe()
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                
                if (responseBody != null) {
                    _user.update { responseBody }
                }
            }
        } catch (e: Exception) {
            Log.e(
                "UserManager",
                "An unexpected error occurred while loading user",
                e
            )
        }
    }
}
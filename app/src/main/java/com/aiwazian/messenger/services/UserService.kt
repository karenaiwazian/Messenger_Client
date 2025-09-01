package com.aiwazian.messenger.services

import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.User
import javax.inject.Inject

class UserService @Inject constructor() {
    
    suspend fun updateProfile(user: User): Boolean {
        val response = RetrofitInstance.api.updateProfile(user)
        return response.isSuccessful
    }
    
    suspend fun getUserById(userId: Int): User? {
        val response = RetrofitInstance.api.getUserById(userId)
        return response.body()
    }
    
    suspend fun checkUsername(username: String): Boolean {
        val response = RetrofitInstance.api.checkUsername(username)
        return response.isSuccessful
    }
    
    suspend fun saveUsername(username: String): Boolean {
        val response = RetrofitInstance.api.saveUsername(username)
        return response.isSuccessful
    }
}
package com.aiwazian.messenger.services

import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.utils.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

class TokenManager {

    private val _token = MutableStateFlow<String?>(null)
    val token = _token.asStateFlow()


    suspend fun getToken(): String {
        val dataStore = DataStoreManager.getInstance()
        return dataStore.getToken().first()
    }

    suspend fun saveToken(token: String) {
        val dataStore = DataStoreManager.getInstance()
        dataStore.saveToken(token)
    }

    suspend fun removeToken() {
        //dataStore.saveToken("")
    }
}
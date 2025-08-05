package com.aiwazian.messenger.services

import com.aiwazian.messenger.utils.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

object TokenManager {
    private val _token = MutableStateFlow("")
    
    private var isAuthorized = false

    private var unauthorizedCallback: (() -> Unit)? = null
    
    fun getToken(): String = _token.value
    
    fun setAuthorized(value: Boolean) {
        isAuthorized = value
    }
    
    fun isAuthorized(): Boolean = isAuthorized
    
    suspend fun init() {
        val dataStore = DataStoreManager.getInstance()
        _token.value = dataStore.getToken().first()
    }
    
    suspend fun saveToken(token: String) {
        _token.value = token
        val dataStore = DataStoreManager.getInstance()
        dataStore.saveToken(token)
    }
    
    suspend fun removeToken() {
        _token.value = ""
        val dataStore = DataStoreManager.getInstance()
        dataStore.saveToken("")
    }
    
    fun setUnauthorizedCallback(callback: () -> Unit) {
        unauthorizedCallback = callback
    }
    
    fun getUnauthorizedCallback(): (() -> Unit)? = unauthorizedCallback
}
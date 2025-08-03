package com.aiwazian.messenger.services

import com.aiwazian.messenger.utils.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

object TokenManager {

    private val _token = MutableStateFlow("")

    private var unauthorizedCallback: (() -> Unit)? = null

    suspend fun init() {
        val dataStore = DataStoreManager.getInstance()
        _token.value = dataStore.getToken().first()
    }

    fun getToken(): String = _token.value

    suspend fun saveToken(token: String) {
        _token.value = token
        val dataStore = DataStoreManager.getInstance()
        dataStore.saveToken(token)
    }

    fun setUnauthorizedCallback(callback: () -> Unit) {
        unauthorizedCallback = callback
    }

    fun getUnauthorizedCallback(): (() -> Unit)? = unauthorizedCallback
}
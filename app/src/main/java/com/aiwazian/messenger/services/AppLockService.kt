package com.aiwazian.messenger.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLockService @Inject constructor() {
    
    private val _isLockApp = MutableStateFlow(false)
    val isLockApp = _isLockApp.asStateFlow()
    
    private val _passcode = MutableStateFlow("")
    val passcode = _passcode.asStateFlow()
    
    private val _hasPasscode = MutableStateFlow(false)
    val hasPasscode = _hasPasscode.asStateFlow()
    
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val dataStorage = DataStoreManager.Companion.getInstance()
    
    init {
        coroutineScope.launch {
            dataStorage.getIsLockApp().collectLatest {
                _isLockApp.value = it
            }
        }
        
        coroutineScope.launch {
            dataStorage.getPasscode().collectLatest {
                _passcode.value = it
                _hasPasscode.value = it.isNotBlank()
            }
        }
    }
    
    suspend fun lockApp() {
        dataStorage.saveIsLockApp(true)
    }
    
    suspend fun unlockApp() {
        dataStorage.saveIsLockApp(false)
    }
    
    suspend fun changePasscode(newPasscode: String) {
        dataStorage.savePasscode(newPasscode)
    }
}
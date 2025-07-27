package com.aiwazian.messenger.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AppLockService {
    private var _isLockApp = MutableStateFlow(false)
    var isLockApp = _isLockApp.asStateFlow()

    private var _passcode = MutableStateFlow("")
    var passcode = _passcode.asStateFlow()

    private var _hasPasscode = MutableStateFlow(false)
    var hasPasscode = _hasPasscode.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val dataStorage = DataStoreManager.getInstance()

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
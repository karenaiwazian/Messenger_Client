package com.aiwazian.messenger.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.utils.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LockScreenViewModel : ViewModel() {
    var onWrongPasscode: () -> Unit = { }

    var passcode by mutableStateOf("")
        private set

    fun onPasscodeChanged(newPasscode: String) {
        if (newPasscode.length <= PasscodeViewModel.MAX_LENGTH_PASSCODE) {
            passcode = newPasscode
        }

        if (passcode.length == PasscodeViewModel.MAX_LENGTH_PASSCODE) {
            checkPasscode()
        }
    }

    fun clearPasscode() {
        passcode = ""
    }

    private fun checkPasscode() {
        viewModelScope.launch {
            val dataStore = DataStoreManager.getInstance()
            val correctPasscode = dataStore.getPasscode().first()

            if (passcode == correctPasscode) {
                clearPasscode()
                unlockApp()
            } else {
                onWrongPasscode()
            }
        }
    }

    private suspend fun unlockApp() {
        val dataStore = DataStoreManager.getInstance()
        dataStore.saveIsLockApp(false)
    }
}
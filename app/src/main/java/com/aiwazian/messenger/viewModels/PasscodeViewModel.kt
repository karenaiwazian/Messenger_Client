package com.aiwazian.messenger.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.services.AppLockService
import kotlinx.coroutines.launch

class PasscodeViewModel : ViewModel() {

    companion object {
        const val MAX_LENGTH_PASSCODE = 4
    }

    var passcode by mutableStateOf("")
        private set

    var onSaveNewPasscode: () -> Unit = { }

    private val appLockService = AppLockService()

    fun onPasscodeChanged(newPasscode: String) {
        if (newPasscode.length <= MAX_LENGTH_PASSCODE) {
            passcode = newPasscode
        }

        if (passcode.length == MAX_LENGTH_PASSCODE) {
            setPasscode()
        }
    }

    suspend fun disablePasscode() {
        appLockService.changePasscode(passcode)
        appLockService.unlockApp()
    }

    private fun setPasscode() {
        viewModelScope.launch {
            appLockService.changePasscode(passcode)
            onSaveNewPasscode()
        }
    }
}
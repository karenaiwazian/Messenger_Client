package com.aiwazian.messenger.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.services.AppLockService
import com.aiwazian.messenger.services.DialogController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PasscodeViewModel : ViewModel() {
    
    companion object {
        const val MAX_LENGTH_PASSCODE = 4
    }
    
    private val _passcode = MutableStateFlow("")
    val passcode = _passcode.asStateFlow()
    
    val disablePasscodeDialog = DialogController()
    
    var onSaveNewPasscode: () -> Unit = { }
    
    private val appLockService = AppLockService()
    
    fun onPasscodeChanged(newPasscode: String) {
        if (newPasscode.length <= MAX_LENGTH_PASSCODE) {
            _passcode.value = newPasscode
        }
        
        if (passcode.value.length == MAX_LENGTH_PASSCODE) {
            setPasscode()
        }
    }
    
    suspend fun disablePasscode() {
        appLockService.changePasscode(_passcode.value)
        appLockService.unlockApp()
    }
    
    private fun setPasscode() {
        viewModelScope.launch {
            appLockService.changePasscode(_passcode.value)
            onSaveNewPasscode()
        }
    }
}
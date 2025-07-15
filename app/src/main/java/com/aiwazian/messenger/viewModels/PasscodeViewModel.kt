package com.aiwazian.messenger.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.utils.Constants
import com.aiwazian.messenger.utils.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PasscodeViewModel : ViewModel() {
    val MAX_LENGTH_PASSCODE = Constants.MAX_LENGTH_PASSCODE

    var passcode by mutableStateOf("")
        private set

    var onSaveNewPasscode: () -> Unit = { }

    init {

    }

    fun onPasscodeChanged(newPasscode: String) {
        if (newPasscode.length <= MAX_LENGTH_PASSCODE) {
            passcode = newPasscode
        }

        if (passcode.length == MAX_LENGTH_PASSCODE) {
            setPasscode()
        }
    }

    suspend fun disablePasscode() {
        val dataStore = DataStoreManager.getInstance()

        dataStore.saveIsLockApp(false)
        dataStore.savePasscode("")
    }

    private fun setPasscode() {
        val dataStore = DataStoreManager.getInstance()

        viewModelScope.launch {
            dataStore.savePasscode(passcode)
            onSaveNewPasscode()
        }
    }
}
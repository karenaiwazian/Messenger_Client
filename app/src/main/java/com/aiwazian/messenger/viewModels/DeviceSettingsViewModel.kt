package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.services.UserService
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.services.TokenManager
import kotlinx.coroutines.launch

class DeviceSettingsViewModel : ViewModel() {

    suspend fun terminateAllOtherSessions(success: () -> Unit, fail: () -> Unit) {
        try {
            val tokenManager = TokenManager()
            val token = tokenManager.getToken()
            val response =
                RetrofitInstance.api.terminateAllSessions(token)
            if (response.isSuccessful) {
                success()
            } else {
                fail()
            }
        } catch (e: Exception) {
            Log.e("DeviceSettings", "${e.message}")
            fail()
        }
    }
}
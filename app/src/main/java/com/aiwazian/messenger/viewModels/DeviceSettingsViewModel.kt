package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.utils.UserManager
import com.aiwazian.messenger.api.RetrofitInstance
import kotlinx.coroutines.launch

class DeviceSettingsViewModel : ViewModel() {

    fun terminateAllOtherSessions(success: () -> Unit, fail: () -> Unit) {
        val token = UserManager.token

        viewModelScope.launch {
            try {
                val response =
                    RetrofitInstance.api.terminateAllSessions("Bearer $token")
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
}
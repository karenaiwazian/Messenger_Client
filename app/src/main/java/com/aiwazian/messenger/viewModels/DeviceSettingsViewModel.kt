package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.api.RetrofitInstance

class DeviceSettingsViewModel : ViewModel() {

    suspend fun terminateAllOtherSessions(success: () -> Unit, fail: () -> Unit) {
        try {
            val response =
                RetrofitInstance.api.terminateAllSessions()
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
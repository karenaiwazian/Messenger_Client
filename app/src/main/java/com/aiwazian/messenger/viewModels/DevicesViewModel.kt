package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.Session
import com.aiwazian.messenger.services.DeviceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DevicesViewModel : ViewModel() {
    
    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions = _sessions.asStateFlow()
    
    private val _isVisibleAutoTerminateDialog = MutableStateFlow(false)
    val isVisibleAutoTerminateDialog = _isVisibleAutoTerminateDialog.asStateFlow()
    
    private val _isVisibleConfirmTerminateDialog = MutableStateFlow(false)
    val isVisibleConfirmTerminateDialog = _isVisibleConfirmTerminateDialog.asStateFlow()
    
    private val _isVisibleBottomSheetDialog = MutableStateFlow(false)
    val isVisibleBottomSheetDialog = _isVisibleBottomSheetDialog.asStateFlow()
    
    private var _confirmDialogAction: (suspend () -> Unit)? = null
    
    private val _openedSession = MutableStateFlow(
        Session(
            0,
            "",
            ""
        )
    )
    
    val openedSession = _openedSession.asStateFlow()
    
    suspend fun terminateSession(sessionId: Int) {
        try {
            val response = RetrofitInstance.api.terminateSession(sessionId)
            
            if (response.isSuccessful) {
                val sessionList = _sessions.value.toMutableList()
                val session = sessionList.find { it.id == sessionId }
                
                sessionList.remove(session)
                
                _sessions.value = sessionList.toList()
            }
        } catch (e: Exception) {
            Log.e(
                "DeviceSettings",
                "${e.message}"
            )
        }
    }
    
    suspend fun getSessions() {
        try {
            val response = RetrofitInstance.api.getSessions()
            
            if (response.isSuccessful) {
                _sessions.value = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e(
                "DeviceSettings",
                "${e.message}"
            )
        }
    }
    
    suspend fun terminateAllOtherSessions() {
        try {
            val response = RetrofitInstance.api.terminateAllSessions()
            
            if (response.isSuccessful) {
                _sessions.value = emptyList()
            }
        } catch (e: Exception) {
            Log.e(
                "DeviceSettings",
                "${e.message}"
            )
        }
    }
    
    fun openSession(sessionId: Int) {
        if (sessionId == 0) {
            val deviceHelper = DeviceHelper()
            val deviceName = deviceHelper.getDeviceName()
            
            _openedSession.value = Session(
                0,
                deviceName,
                ""
            )
        } else {
            _openedSession.value = _sessions.value.first { it.id == sessionId }
        }
    }
    
    fun setConfirmDialogAction(action: suspend () -> Unit) {
        _confirmDialogAction = action
    }
    
    fun getConfirmDialogAction() = _confirmDialogAction
    
    fun showAutoTerminateSessionDialog() {
        _isVisibleAutoTerminateDialog.value = true
    }
    
    fun hideAutoTerminateSessionDialog() {
        _isVisibleAutoTerminateDialog.value = false
    }
    
    fun showConfirmTerminateSessionDialog() {
        _isVisibleConfirmTerminateDialog.value = true
    }
    
    fun hideConfirmTerminateSessionDialog() {
        _isVisibleConfirmTerminateDialog.value = false
    }
    
    fun showBottomSheetDialog() {
        _isVisibleBottomSheetDialog.value = true
    }
    
    fun hideBottomSheetDialog() {
        _isVisibleBottomSheetDialog.value = false
    }
}
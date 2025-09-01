package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.data.Session
import com.aiwazian.messenger.services.DeviceHelper
import com.aiwazian.messenger.services.DialogController
import com.aiwazian.messenger.services.SessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val deviceHelper: DeviceHelper,
    private val sessionService: SessionService
) : ViewModel() {
    
    private val _currentSession = MutableStateFlow(Session())
    val currentSession = _currentSession.asStateFlow()
    
    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions = _sessions.asStateFlow()
    
    val terminateSessionDialog = DialogController()
    
    val sessionInfoDialog = DialogController()
    
    private var _confirmDialogAction: (suspend () -> Unit)? = null
    
    private val _openedSession = MutableStateFlow(
        Session(
            0, "", ""
        )
    )
    
    val openedSession = _openedSession.asStateFlow()
    
    init {
        val deviceName = deviceHelper.getDeviceName()
        
        _currentSession.update { session ->
            session.deviceName = deviceName
            session
        }
    }
    
    suspend fun terminateSession(sessionId: Int) {
        try {
            val isTerminated = sessionService.terminateSession(sessionId)
            
            if (isTerminated) {
                val sessionList = _sessions.value.toMutableList()
                val session = sessionList.find { it.id == sessionId }
                
                sessionList.remove(session)
                
                _sessions.value = sessionList.toList()
            }
        } catch (e: Exception) {
            Log.e(
                "DeviceSettings", "${e.message}"
            )
        }
    }
    
    suspend fun getSessions() {
        try {
            val sessions = sessionService.getSessions()
            
            if (sessions?.isNotEmpty() == true) {
                _sessions.value = sessions
            }
        } catch (e: Exception) {
            Log.e(
                "DeviceSettings", "${e.message}"
            )
        }
    }
    
    suspend fun terminateAllOtherSessions() {
        try {
            val isTerminated = sessionService.terminateAllSessions()
            
            if (isTerminated) {
                _sessions.value = emptyList()
            }
        } catch (e: Exception) {
            Log.e(
                "DeviceSettings", "${e.message}"
            )
        }
    }
    
    fun openSession(sessionId: Int) {
        if (sessionId == 0) {
            val deviceName = deviceHelper.getDeviceName()
            
            _openedSession.value = Session(
                0, deviceName, ""
            )
        } else {
            _openedSession.value = _sessions.value.first { it.id == sessionId }
        }
    }
    
    fun setConfirmDialogAction(action: suspend () -> Unit) {
        _confirmDialogAction = action
    }
    
    fun getConfirmDialogAction() = _confirmDialogAction
}
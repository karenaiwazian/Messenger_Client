package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.AuthRequest
import com.aiwazian.messenger.data.RegisterRequest
import com.aiwazian.messenger.services.DeviceHelper
import com.aiwazian.messenger.services.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    
    private val _login = MutableStateFlow("")
    val login = _login.asStateFlow()
    
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    
    private val _loginFieldError = MutableStateFlow<String?>(null)
    val loginFieldError = _loginFieldError.asStateFlow()
    
    private val _passwordFieldError = MutableStateFlow<String?>(null)
    val passwordFieldError = _passwordFieldError.asStateFlow()
    
    private val _isUserFound = MutableStateFlow(false)
    
    fun setUserFoundState(state: Boolean) {
        _isUserFound.value = state
    }
    
    fun getUserFoundState(): Boolean {
        return _isUserFound.value
    }
    
    fun onLoginChanged(newLogin: String) {
        _login.value = newLogin
        clearError()
    }
    
    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        clearError()
    }
    
    suspend fun findUserByLogin(): Boolean {
        try {
            val response = RetrofitInstance.api.findUserByLogin(login = _login.value)
            
            if (!response.isSuccessful) {
                return false
            }
            
            return response.code() == 200
        } catch (e: Exception) {
            Log.e(
                "AuthViewModel",
                "findUserByLogin: ${e.message}"
            )
            
            return false
        }
    }
    
    suspend fun onLoginClicked(): Boolean {
        try {
            val deviceHelper = DeviceHelper()
            val deviceName = deviceHelper.getDeviceName()
            
            val response = RetrofitInstance.api.login(
                AuthRequest(
                    _login.value,
                    _password.value,
                    deviceName
                )
            )
            
            if (!response.isSuccessful) {
                return false
            }
            
            val code = response.code()
            
            if (code != 200) {
                return false
            }
            
            val body = response.body()
            
            if (body == null) {
                return false
            }
            
            val token = body.message
            
            TokenManager.saveToken(token)
            TokenManager.setAuthorized(true)
            
            return true
        } catch (e: Exception) {
            Log.e(
                "AuthViewModel",
                "onLoginClicked: ${e.message}"
            )
            
            return false
        }
    }
    
    suspend fun onRegisterClicked(): Boolean {
        try {
            val requestBody =
                RegisterRequest(
                    _login.value,
                    _password.value
                )
            
            val response = RetrofitInstance.api.register(requestBody)
            
            if (!response.isSuccessful) {
                return false
            }
            
            return response.code() == 200
        } catch (e: Exception) {
            Log.e(
                "AuthViewModel",
                "onRegisterClicked: ${e.message}"
            )
            
            return false
        }
    }
    
    fun checkValidLogin(): Boolean {
        if (_login.value.isBlank()) {
            _loginFieldError.value = "Введите логин"
            return false
        }
        
        if (_login.value.trim().length < 5) {
            _loginFieldError.value = "Минимум 5 символов"
            return false
        }
        
        _loginFieldError.value = null
        
        return true
    }
    
    fun checkValidPassword(): Boolean {
        if (_password.value.isBlank()) {
            _passwordFieldError.value = "Введите пароль"
            return false
        }
        
        if (_password.value.trim().length < 5) {
            _passwordFieldError.value = "Минимум 5 символов"
            return false
        }
        
        _passwordFieldError.value = null
        
        return true
    }
    
    private fun clearError() {
        _loginFieldError.value = null
        _passwordFieldError.value = null
    }
}

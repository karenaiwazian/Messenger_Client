package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.utils.DataStoreManager
import com.aiwazian.messenger.utils.DeviceHelper
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.AuthRequest
import com.aiwazian.messenger.data.CheckVerificationCodeRequest
import com.aiwazian.messenger.data.FindUserRequest
import com.aiwazian.messenger.data.RegisterRequest
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    var login by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var loginError by mutableStateOf(false)
        private set

    var passwordError by mutableStateOf(false)
        private set

    var verificationCode by mutableStateOf("")
        private set

    var isAuthSuccessful by mutableStateOf(false)
        private set

    var isUserFound by mutableStateOf(false)
        private set

    var onCorrectVerificationCode: (() -> Unit)? = null

    var onWrongVerificationCode: (() -> Unit)? = null

    var onClearError: (() -> Unit)? = null

    fun onLoginChanged(newEmail: String) {
        login = newEmail
        loginError = false
        errorMessage = null
    }

    fun onPasswordChanged(newPassword: String) {
        password = newPassword
        passwordError = false
        errorMessage = null
    }

    fun onVerificationCodeChanged(newCode: String) {
        if (newCode.length <= 6) {
            verificationCode = newCode
            onClearError?.invoke()
        }

        if (verificationCode.length == 6) {
            checkVerificationCode()
            onClearError?.invoke()
        }
    }

    fun checkVerificationCode() {
        viewModelScope.launch {
            try {
                val request = CheckVerificationCodeRequest(login = login, code = verificationCode)
                val response = RetrofitInstance.api.checkVerificationCode(request = request)

                if (!response.isSuccessful) {
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    return@launch
                }

                if (body.success) {
                    onCorrectVerificationCode?.invoke()
                } else {
                    onWrongVerificationCode?.invoke()
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "checkVerificationCode: ${e.message}")
            }
        }
    }

    fun findUserByLogin(find: () -> Unit, notFind: () -> Unit, error: () -> Unit) {
        viewModelScope.launch {
            try {
                val response =
                    RetrofitInstance.api.findUserByLogin(request = FindUserRequest(login = login))

                if (!response.isSuccessful) {
                    error()
                    isUserFound = false
                    return@launch
                }

                val body = response.body()

                if (body == null) {
                    isUserFound = false
                    notFind()
                    return@launch
                }

                if (body.success) {
                    isUserFound = true
                    find()
                } else {
                    isUserFound = false
                    notFind()
                }

            } catch (e: Exception) {
                error()
                Log.e("AuthViewModel", "findUserByLogin: ${e.message}")
            }
        }
    }

    fun onLoginClicked(success: () -> Unit, error: () -> Unit) {
        clearError()
        if (!isValidData()) {
            error()
            return
        }

        errorMessage = null

        viewModelScope.launch {
            try {
                val deviceHelper = DeviceHelper()
                val device = deviceHelper.getDeviceName()

                val response = RetrofitInstance.api.login(AuthRequest(login, password, device))

                if (!response.isSuccessful) {
                    loginError = true
                    passwordError = true
                    error()
                    return@launch
                }

                val token = response.body()?.token

                if (token == null) {
                    error()
                    return@launch
                }

                val store = DataStoreManager.Companion.getInstance()
                store.saveToken(token)
                isAuthSuccessful = true
                success()
            } catch (e: Exception) {
                error()
                Log.e("AuthViewModel", "onLoginClicked: ${e.message}")
            }
        }
    }

    fun onRegisterClicked(success: () -> Unit, error: () -> Unit) {
        clearError()
        if (!isValidData()) {
            error()
            return
        }

        errorMessage = null

        viewModelScope.launch {
            try {
                val deviceHelper = DeviceHelper()
                val device = deviceHelper.getDeviceName()
                val requestBody =
                    RegisterRequest(login = login, password = password, deviceName = device)

                val response = RetrofitInstance.api.register(requestBody)

                if (!response.isSuccessful) {
                    loginError = true
                    passwordError = true
                    error()
                    return@launch
                }

                val token = response.body()?.token

                if (token != null) {
                    success()
                    return@launch
                }

                loginError = true
                passwordError = true
            } catch (e: Exception) {
                Log.e("AuthViewModel", "onRegisterClicked: ${e.message}")
            }
        }
    }

    private fun isValidData(): Boolean {
        if (login.isBlank() && password.isBlank()) {
            loginError = true
            passwordError = true
        }

        if (login.isBlank()) {
            loginError = true
        }

        if (login.trim().length < 5) {
            loginError = true
        }

        if (password.isBlank()) {
            passwordError = true
        }

        if (password.trim().length < 5) {
            passwordError = true
        }

        return !loginError && !passwordError
    }

    private fun clearError() {
        errorMessage = null
        loginError = false
        passwordError = false
    }
}

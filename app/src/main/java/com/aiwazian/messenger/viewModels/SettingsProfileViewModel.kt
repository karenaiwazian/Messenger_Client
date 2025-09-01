package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.services.DialogController
import com.aiwazian.messenger.services.UserManager
import com.aiwazian.messenger.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsProfileViewModel @Inject constructor(private val userService: UserService) :
    ViewModel() {
    
    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()
    
    val dataOfBirthDialog = DialogController()
    
    init {
        viewModelScope.launch {
            UserManager.user.collectLatest { collect ->
                _user.update { collect }
            }
        }
    }
    
    fun onChangeFirstName(newName: String) {
        _user.value = _user.value.copy(firstName = newName)
    }
    
    fun onChangeLastName(newName: String) {
        _user.value = _user.value.copy(lastName = newName)
    }
    
    fun onChangeBio(newBio: String) {
        _user.value = _user.value.copy(bio = newBio)
    }
    
    fun onChangeDateOfBirth(newDate: Long?) {
        _user.value = _user.value.copy(dateOfBirth = newDate)
    }
    
    suspend fun save() {
        try {
            val response = userService.updateProfile(_user.value)
            
            if (response) {
                UserManager.updateUserInfo(_user.value)
            }
        } catch (e: Exception) {
            Log.e(
                "UserManager",
                "Error saving user data",
                e
            )
        }
    }
}
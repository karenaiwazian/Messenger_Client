package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.services.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsProfileViewModel : ViewModel() {
    
    private val _firstName = MutableStateFlow("")
    val firstName = _firstName.asStateFlow()
    
    private val _lastName = MutableStateFlow("")
    val lastName = _lastName.asStateFlow()
    
    private val _username = MutableStateFlow<String?>(null)
    val username = _username.asStateFlow()
    
    private val _bio = MutableStateFlow("")
    val bio = _bio.asStateFlow()
    
    private val _dateOfBirth = MutableStateFlow<Long?>(null)
    val dateOfBirth = _dateOfBirth.asStateFlow()
    
    private val _isVisibleDataPicker = MutableStateFlow(false)
    val isVisibleDataPicker = _isVisibleDataPicker.asStateFlow()
    
    init {
        val user = UserManager.user.value
        _firstName.value = user.firstName
        _lastName.value = user.lastName
        _bio.value = user.bio
        _username.value = user.username
        _dateOfBirth.value = user.dateOfBirth
    }
    
    fun onChangeFirstName(newName: String) {
        _firstName.value = newName
    }
    
    fun onChangeLastName(newName: String) {
        _lastName.value = newName
    }
    
    fun onChangeUsername(newUsername: String) {
        _username.value = newUsername
    }
    
    fun onChangeBio(newBio: String) {
        _bio.value = newBio
    }
    
    fun onChangeDateOfBirth(newDate: Long?) {
        _dateOfBirth.value = newDate
    }
    
    fun showDataPicker() {
        _isVisibleDataPicker.value = true
    }
    
    fun hideDataPicker() {
        _isVisibleDataPicker.value = false
    }
    
    suspend fun save() {
        try {
            val user = User(
                firstName = _firstName.value,
                lastName = _lastName.value,
                username = _username.value,
                bio = _bio.value,
                dateOfBirth = _dateOfBirth.value
            )
            
            val response = RetrofitInstance.api.updateProfile(user)
            
            if (response.isSuccessful) {
                UserManager.updateUserInfo(user)
                Log.d("UserManager", "change is success $user")
            }
        } catch (e: Exception) {
            Log.e("UserManager", "Error saving user data", e)
        }
    }
}
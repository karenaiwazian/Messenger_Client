package com.aiwazian.messenger.viewModels

import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.services.UserManager
import com.aiwazian.messenger.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val userService: UserService) : ViewModel() {
    
    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()
    
    suspend fun open(profileId: Int) {
        if (profileId == UserManager.user.value.id) {
            UserManager.user.collectLatest { collect ->
                _user.update { collect }
            }
        }
        
        val response = userService.getUserById(profileId)
        
        if (response != null) {
            _user.update { response }
        }
    }
}
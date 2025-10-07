package com.aiwazian.messenger.viewModels

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.MainActivity
import com.aiwazian.messenger.R
import com.aiwazian.messenger.database.repository.ChannelRepository
import com.aiwazian.messenger.database.repository.UserRepository
import com.aiwazian.messenger.interfaces.Profile
import com.aiwazian.messenger.services.DialogController
import com.aiwazian.messenger.services.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val channelRepository: ChannelRepository
) : ViewModel() {
    
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile = _profile.asStateFlow()
    
    val blockUserDialog = DialogController()
    
    val startSecretChatDialog = DialogController()
    
    suspend fun open(profileId: Int) {
        if (profileId < 0) {
            val channel = channelRepository.get(profileId)
            _profile.update { channel }
            return
        }
        
        if (profileId == UserManager.user.value.id) {
            viewModelScope.launch {
                UserManager.user.collectLatest { collect ->
                    _profile.update { collect }
                }
            }
            
            return
        }
        
        val user = userRepository.getById(profileId)
        
        if (user != null) {
            _profile.update { user }
        }
    }
    
    fun createShortcut(context: Context) {
        // TODO shortcut
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)
        
        val shortcut = ShortcutInfo.Builder(
            context,
            "shortcut_chat_1"
        )
            .setShortLabel("Chat")
            .setLongLabel("Chat Long")
            .setIcon(
                Icon.createWithResource(
                    context,
                    R.mipmap.new_app_icon
                )
            )
            .setIntent(
                Intent(
                    context,
                    MainActivity::class.java
                ).apply {
                    action = Intent.ACTION_VIEW
                    putExtra(
                        "chatId",
                        1
                    )
                })
            .build()
        
        shortcutManager.pushDynamicShortcut(shortcut)
        shortcutManager.dynamicShortcuts = listOf(shortcut)
    }
}
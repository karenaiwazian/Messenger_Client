package com.aiwazian.messenger.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aiwazian.messenger.customType.PrimaryColorOption
import com.aiwazian.messenger.customType.ThemeOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ThemeService {
    private var _currentTheme = MutableStateFlow(ThemeOption.SYSTEM)
    var currentTheme = _currentTheme.asStateFlow()

    private var _primaryColor = MutableStateFlow(PrimaryColorOption.Blue)
    var primaryColor = _primaryColor.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val dataStorage = DataStoreManager.getInstance()

    init {
        coroutineScope.launch {
            dataStorage.getTheme().collectLatest { themeName ->
                _currentTheme.value = ThemeOption.fromString(themeName)
            }
        }
        coroutineScope.launch {
            dataStorage.getPrimaryColor().collectLatest { colorName ->
                _primaryColor.value = PrimaryColorOption.fromString(colorName)
            }
        }
    }

    suspend fun changeTheme(theme: ThemeOption) {
        dataStorage.saveTheme(theme)
    }

    suspend fun changePrimaryColor(color: PrimaryColorOption) {
        dataStorage.savePrimaryColor(color.name)
    }
}
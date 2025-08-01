package com.aiwazian.messenger.utils

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
    private var _dynamicColor = MutableStateFlow(false)
    var dynamicColor = _dynamicColor.asStateFlow()

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

        coroutineScope.launch {
            dataStorage.getDynamicColor().collectLatest { dynamicColor ->
                _dynamicColor.value = dynamicColor
            }
        }
    }

    suspend fun setDynamicColor(dynamicColor: Boolean) {
        dataStorage.saveDynamicColor(dynamicColor)
    }

    suspend fun setTheme(theme: ThemeOption) {
        dataStorage.saveTheme(theme)
    }

    suspend fun setPrimaryColor(color: PrimaryColorOption) {
        dataStorage.savePrimaryColor(color.name)
    }
}
package com.aiwazian.messenger.services

import com.aiwazian.messenger.customType.PrimaryColorOption
import com.aiwazian.messenger.customType.ThemeOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeService @Inject constructor() {
    
    private val _currentTheme = MutableStateFlow(ThemeOption.SYSTEM)
    val currentTheme = _currentTheme.asStateFlow()
    
    private val _primaryColor = MutableStateFlow(PrimaryColorOption.Blue)
    val primaryColor = _primaryColor.asStateFlow()
    
    private val _dynamicColor = MutableStateFlow(false)
    val dynamicColor = _dynamicColor.asStateFlow()
    
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val dataStorage = DataStoreManager.Companion.getInstance()
    
    init {
        coroutineScope.launch {
            dataStorage.getTheme().collectLatest { themeName ->
                _currentTheme.value = ThemeOption.Companion.fromString(themeName)
            }
        }
        
        coroutineScope.launch {
            dataStorage.getPrimaryColor().collectLatest { colorName ->
                _primaryColor.value = PrimaryColorOption.Companion.fromString(colorName)
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
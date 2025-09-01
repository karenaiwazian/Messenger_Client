package com.aiwazian.messenger.viewModels

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.data.ScreenEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NavigationViewModel() : ViewModel() {
    
    private val _screenStack = MutableStateFlow<List<ScreenEntry>>(emptyList())
    val screenStack = _screenStack.asStateFlow()
    
    private val _offsetStack =
        MutableStateFlow<List<Animatable<Float, AnimationVector1D>>>(emptyList())
    
    val offsetStack = _offsetStack.asStateFlow()
    
    val tweenDurationMillis = 200
    
    var screenWidth = 0f
    
    fun addScreenInStack(
        canGoBackBySwipe: Boolean = true,
        screen: @Composable () -> Unit
    ) {
        _screenStack.value += ScreenEntry(
            screen,
            canGoBackBySwipe
        )
        _offsetStack.value += Animatable(screenWidth)
    }
    
    fun removeLastScreenInStack() {
        if (_screenStack.value.isEmpty() && _offsetStack.value.isEmpty()) {
            return
        }
        
        viewModelScope.launch {
            val screenStack = _screenStack.value.toMutableList()
            val offsetStack = _offsetStack.value.toMutableList()
            
            val lastIndex = screenStack.lastIndex
            
            if (lastIndex >= offsetStack.size) {
                return@launch
            }
            
            val topOffset = offsetStack[lastIndex]
            
            withContext(AndroidUiDispatcher.Main) {
                topOffset.animateTo(
                    screenWidth,
                    tween(tweenDurationMillis)
                )
            }
            
            if (lastIndex < screenStack.size && lastIndex < offsetStack.size) {
                screenStack.removeAt(lastIndex)
                offsetStack.removeAt(lastIndex)
                _screenStack.value = screenStack
                _offsetStack.value = offsetStack
            }
        }
    }
}
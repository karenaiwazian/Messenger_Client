package com.aiwazian.messenger.viewModels

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.data.ScreenEntry
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NavigationViewModel() : ViewModel() {
    var screenStack = mutableStateListOf<ScreenEntry>()
        private set
    var offsetStack = mutableStateListOf<Animatable<Float, AnimationVector1D>>()
        private set
    val tweenDurationMillis: Int = 200
    var screenWidth: Float = 0f

    fun addScreenInStack(canGoBackBySwipe: Boolean = true, screen: @Composable () -> Unit) {
        screenStack.add(ScreenEntry(screen, canGoBackBySwipe))
        offsetStack.add(Animatable(screenWidth))
    }

    fun removeLastScreenInStack() {
        if (screenStack.isEmpty() && offsetStack.isEmpty()) {
            return
        }

        viewModelScope.launch {
            val lastIndex = screenStack.lastIndex

            if (lastIndex >= offsetStack.size) {
                return@launch
            }

            val topOffset = offsetStack[lastIndex]

            withContext(context = AndroidUiDispatcher.Main) {
                topOffset.animateTo(
                    screenWidth,
                    tween(tweenDurationMillis)
                )
            }

            if (lastIndex < screenStack.size && lastIndex < offsetStack.size) {
                screenStack.removeAt(lastIndex)
                offsetStack.removeAt(lastIndex)
            }
        }
    }
}
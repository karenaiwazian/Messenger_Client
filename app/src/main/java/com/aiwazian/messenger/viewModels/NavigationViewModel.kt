package com.aiwazian.messenger.viewModels

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class NavigationViewModel() : ViewModel() {
    var screenStack = mutableStateListOf<@Composable () -> Unit>()
    var offsetStack = mutableStateListOf<Animatable<Float, AnimationVector1D>>()
    val tweenDurationMillis: Int = 200
    var screenWidth: Float = 0f


    fun addScreenInStack(screen: @Composable () -> Unit) {
        screenStack.add(screen)
        offsetStack.add(Animatable(screenWidth))
    }

    suspend fun removeLastScreenInStack(withAnimation: Boolean = true) {
        if (withAnimation) {
            val topOffset = offsetStack[screenStack.lastIndex]
            topOffset.animateTo(screenWidth, tween(tweenDurationMillis))
            topOffset.snapTo(screenWidth)
        }
        screenStack.removeAt(screenStack.lastIndex)
        offsetStack.removeAt(offsetStack.lastIndex)
    }
}
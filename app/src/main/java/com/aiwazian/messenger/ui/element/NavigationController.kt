package com.aiwazian.messenger.ui.element

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.aiwazian.messenger.removeLastScreenFromStack
import com.aiwazian.messenger.ui.MainScreen
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.viewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun NavigationController() {
    val screenStack = viewModel.screenStack
    val offsetStack = viewModel.offsetStack
    val scope = rememberCoroutineScope()
    val tweenDurationMillis = viewModel.tweenDurationMillis
    val screenWidthPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }

    viewModel.screenWidth = screenWidthPx

    val keyboardController = LocalSoftwareKeyboardController.current
    val colors = LocalCustomColors.current

    Box(
        Modifier
            .fillMaxSize()
            .background(colors.secondary)
    ) {
        MainScreen()

        screenStack.forEachIndexed { index, screenContent ->
            val offsetX = offsetStack[index]
            val isTop = index == screenStack.lastIndex

            BackHandler(enabled = screenStack.isNotEmpty()) {
                if (screenStack.isNotEmpty()) {
                    removeLastScreenFromStack()
                }
            }

            LaunchedEffect(screenContent) {
                offsetX.animateTo(0f, tween(tweenDurationMillis))
            }

            val backgroundAlpha = ((1f - (offsetX.value / screenWidthPx)).coerceIn(0f, 1f)) * 0.5f

            BoxShadow(index, backgroundAlpha)

            Box(
                Modifier
                    .fillMaxSize()
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .background(colors.secondary)
                    .zIndex(index + 0.2f)
                    .then(
                        if (isTop) Modifier.draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                keyboardController?.hide()
                                scope.launch {
                                    offsetX.snapTo((offsetX.value + delta).coerceAtLeast(0f))
                                }
                            },
                            onDragStopped = {
                                scope.launch {
                                    if (offsetX.value > screenWidthPx / 4) {
                                        removeLastScreenFromStack()
                                    } else {
                                        offsetX.animateTo(0f, tween(tweenDurationMillis))
                                    }
                                }
                            }
                        ) else Modifier
                    )
            ) {
                screenContent()
            }
        }
    }
}

@Composable
private fun BoxShadow(index: Int, backgroundAlpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = backgroundAlpha))
            .zIndex(index + 0.1f)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { }
    )
}
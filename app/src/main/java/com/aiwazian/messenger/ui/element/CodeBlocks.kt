package com.aiwazian.messenger.ui.element

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiwazian.messenger.ui.theme.LocalCustomColors

@Composable
fun CodeBlocks(count: Int, code: String) {
    val colors = LocalCustomColors.current

    var borderColor by remember { mutableStateOf(colors.textHint) }

    val durationMills = 150

    val fadeInSpec = fadeIn(tween(durationMills))

    val fadeOutSpec = fadeOut(tween(durationMills))

    val inputAnimation = slideInVertically(
        tween(durationMills)
    ) { it } + fadeInSpec togetherWith slideOutVertically(
        tween(durationMills)
    ) { -it } + fadeOutSpec

    val outputAnimation = slideInVertically(
        tween(durationMills)
    ) { -it } + fadeInSpec togetherWith slideOutVertically(
        tween(durationMills)
    ) { it } + fadeOutSpec

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(times = count) { index ->
            val char = code.getOrNull(index)?.toString() ?: ""

            val isCurrent = index == code.length && char.isEmpty()

            val cellBorderColor by animateColorAsState(
                targetValue = if (isCurrent) colors.primary else borderColor,
                animationSpec = tween(
                    durationMillis = 200,
                    easing = FastOutSlowInEasing
                )
            )

            Column(
                modifier = Modifier
                    .width(44.dp)
                    .height(48.dp)
                    .border(
                        width = 2.dp,
                        color = cellBorderColor,
                        shape = RoundedCornerShape(8.dp)
                    ),
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        if (targetState > initialState) {
                            inputAnimation
                        } else {
                            outputAnimation
                        }.using(SizeTransform(clip = true))
                    }
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = colors.text,
                        lineHeight = 40.sp,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
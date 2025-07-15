package com.aiwazian.messenger.ui.login

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.aiwazian.messenger.utils.VibrateService
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.utils.VibrationPattern
import com.aiwazian.messenger.viewModels.AuthViewModel

@Composable
fun VerificationCodeScreen(navController: NavHostController, viewModel: AuthViewModel) {
    Content(navController, viewModel)
}

@Composable
private fun Content(navController: NavHostController, viewModel: AuthViewModel) {
    val colors = LocalCustomColors.current

    var keyboardHeight by remember { mutableStateOf(0.dp) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.secondary,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                modifier = Modifier.padding(bottom = keyboardHeight + 16.dp),
                containerColor = colors.primary,
                shape = CircleShape
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            CodeBlocks(viewModel = viewModel)

            Board(
                value = viewModel.verificationCode,
                onChange = viewModel::onVerificationCodeChanged,
                onHeightMeasured = { height ->
                    keyboardHeight = height
                }
            )
        }
    }
}

@Composable
private fun CodeBlocks(viewModel: AuthViewModel) {
    val colors = LocalCustomColors.current

    var borderColor by remember { mutableStateOf(colors.textHint) }

    viewModel.onCorrectVerificationCode = {
        borderColor = Color.Green
    }

    viewModel.onWrongVerificationCode = {
        borderColor = Color.Red
    }

    viewModel.onClearError = {
        borderColor = colors.textHint
    }

    Row(
        modifier = Modifier.padding(top = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(6) { index ->
            val char = viewModel.verificationCode.getOrNull(index)?.toString() ?: ""

            val isCurrent = index == viewModel.verificationCode.length && char.isEmpty()

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
                        2.dp,
                        cellBorderColor,
                        RoundedCornerShape(8.dp)
                    ),
                verticalArrangement = Arrangement.Center
            ) {
                val durationMills = 150

                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInVertically(
                                animationSpec = tween(durationMills)
                            ) { it } + fadeIn(animationSpec = tween(durationMills)) togetherWith slideOutVertically(
                                animationSpec = tween(durationMills)
                            ) { -it } + fadeOut(animationSpec = tween(durationMills))
                        } else {
                            slideInVertically(
                                animationSpec = tween(durationMills)
                            ) { -it } + fadeIn(animationSpec = tween(durationMills)) togetherWith slideOutVertically(
                                animationSpec = tween(durationMills)
                            ) { it } + fadeOut(animationSpec = tween(durationMills))
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

@Composable
private fun Board(value: String, onChange: (String) -> Unit, onHeightMeasured: (Dp) -> Unit) {
    val colors = LocalCustomColors.current

    val context = LocalContext.current

    val localDensity = LocalDensity.current

    val vibrateService = VibrateService(context)

    Column(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, bottom = 10.dp)
            .onGloballyPositioned { coordinates ->
                val heightPx = coordinates.size.height
                val heightDp = with(localDensity) {
                    heightPx.toDp()
                }
                onHeightMeasured(heightDp)
            },
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
        )

        keys.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { key ->
                    NumberButton(
                        onClick = {
                            onChange(value + key)
                            vibrateService.vibrate(
                                pattern = VibrationPattern.TactileResponse
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = key, color = colors.text, fontSize = 18.sp, lineHeight = 30.sp
                        )
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Box(modifier = Modifier.weight(1f))

            NumberButton(
                onClick = {
                    onChange(value + "0")
                    vibrateService.vibrate(
                        pattern = VibrationPattern.TactileResponse
                    )
                },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "0", color = colors.text, fontSize = 18.sp, lineHeight = 30.sp
                )
            }

            NumberButton(
                onClick = {
                    onChange(value.dropLast(1))
                    vibrateService.vibrate(
                        pattern = VibrationPattern.TactileResponse
                    )
                },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("", lineHeight = 30.sp)
                Icon(
                    Icons.AutoMirrored.Outlined.Backspace,
                    contentDescription = null,
                    tint = colors.text
                )
            }
        }
    }
}

@Composable
private fun NumberButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = LocalCustomColors.current

    val clickedColor = colors.textHint

    TextButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = clickedColor,
            containerColor = colors.background
        )
    ) {
        content()
    }
}
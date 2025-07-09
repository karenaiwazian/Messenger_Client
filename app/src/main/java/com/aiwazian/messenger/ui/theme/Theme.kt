package com.aiwazian.messenger.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.aiwazian.messenger.data.CustomColors
import com.aiwazian.messenger.customType.PrimaryColorOption
import com.aiwazian.messenger.customType.ThemeOption

private val DarkColorScheme = CustomColors(
    secondary = Color(0xFF000000),
    background = Color(0xFF191919),
    primary = Color(0xFF0096E6),
    text = Color(0xFFFFFFFF),
    textHint = Color(0xFF646464),
    topAppBarBackground = Color(0xFF1E1E1E),
    sendMessageTimeBackground = Color(0x80646464),
    danger = Color(0xFFC85050),
    dangerBackground = Color(0xFFB43232)
)

private val LightColorScheme = CustomColors(
    secondary = Color(0xFFF0F0F0),
    background = Color(0xFFFFFFFF),
    primary = Color(0xFF0096E6),
    text = Color(0xFF000000),
    textHint = Color(0xFF646464),
    topAppBarBackground = Color(0xFFFFFFFF),
    sendMessageTimeBackground = Color(0x80646464),
    danger = Color(0xFFC80000),
    dangerBackground = Color(0xFFC83232)
)

val LocalCustomColors = staticCompositionLocalOf {
    LightColorScheme
}

@Composable
fun ApplicationTheme(
    theme: ThemeOption = ThemeOption.SYSTEM,
    primaryColor: Color = PrimaryColorOption.Blue.color,
    content: @Composable () -> Unit,
) {
    val isDark = when (theme) {
        ThemeOption.DARK -> true
        ThemeOption.LIGHT -> false
        ThemeOption.SYSTEM -> isSystemInDarkTheme()
    }

    val customColors = if (isDark) {
        DarkColorScheme.copy(primary = primaryColor)
    } else {
        LightColorScheme.copy(primary = primaryColor)
    }

    val view = LocalView.current
    val activity = view.context as Activity

    SideEffect {
        val window = activity.window
        val insetsController = WindowCompat.getInsetsController(window, view)

        window.statusBarColor = Color.Transparent.toArgb()

        insetsController.isAppearanceLightStatusBars = !isDark
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            content = content
        )
    }
}
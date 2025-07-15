package com.aiwazian.messenger.ui.settings.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.customType.PrimaryColorOption
import com.aiwazian.messenger.customType.ThemeOption
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.utils.ThemeService
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsChatScreen() {
    Content()
}

@Composable
private fun Content() {
    val navViewModel: NavigationViewModel = viewModel()
    val colors = LocalCustomColors.current

    val scrollState = rememberScrollState()

    val initialTopBarColor = colors.secondary
    val scrolledTopBarColor = colors.topAppBarBackground

    val topBarColor = if (scrollState.value > 0) {
        scrolledTopBarColor
    } else {
        initialTopBarColor
    }

    Scaffold(
        topBar = {
            TopBar(topBarColor)
        },
        containerColor = colors.secondary
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.secondary)
                .verticalScroll(scrollState)
        ) {
            val themeService = ThemeService()
            val primaryColor = themeService.primaryColor.collectAsState().value
            val theme = when (themeService.currentTheme.collectAsState().value) {
                ThemeOption.DARK -> "Включена"
                ThemeOption.LIGHT -> "Отключена"
                else -> "Как в системе"
            }

            SectionHeader(title = stringResource(R.string.color_theme))

            SectionContainer {
                val coroutineScope = rememberCoroutineScope()

                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    PrimaryColorOption.entries.forEach { option ->
                        RadioButton(
                            modifier = Modifier.scale(1.5f),
                            selected = primaryColor == option,
                            onClick = {
                                coroutineScope.launch {
                                    themeService.changePrimaryColor(option)
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = option.color,
                                unselectedColor = option.color,
                            )
                        )
                    }
                }
            }

            SectionContainer {
                SectionItem(
                    text = stringResource(R.string.dark_theme),
                    primaryText = theme,
                    onClick = {
                        navViewModel.addScreenInStack {
                            SettingsDarkThemeScreen()
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(backgroundColor: Color) {
    val navViewModel: NavigationViewModel = viewModel()
    val colors = LocalCustomColors.current

    PageTopBar(
        title = {
            Text(stringResource(R.string.design))
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.text,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = colors.text
        )
    )
}

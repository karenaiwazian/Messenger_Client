package com.aiwazian.messenger.ui.settings

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aiwazian.messenger.DataStoreManager
import com.aiwazian.messenger.R
import com.aiwazian.messenger.addScreenInStack
import com.aiwazian.messenger.customType.PrimaryColorOption
import com.aiwazian.messenger.customType.ThemeOption
import com.aiwazian.messenger.removeLastScreenFromStack
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Composable
fun ChatSettings() {
    Content()
}

@Composable
private fun Content() {
    val colors = LocalCustomColors.current

    val scrollState = rememberScrollState()

    val initialTopBarColor = colors.secondary
    val scrolledTopBarColor = colors.topAppBarBackground

    val topBarColor = if (scrollState.value > 0) {
        scrolledTopBarColor
    } else {
        initialTopBarColor
    }

    Scaffold(topBar = { TopBar(topBarColor) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.secondary)
                .verticalScroll(scrollState)
        ) {
            val dataStoreManager = DataStoreManager.getInstance()
            var theme by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                dataStoreManager.getTheme().collect {
                    theme = when (it) {
                        ThemeOption.DARK -> "Включена"
                        ThemeOption.LIGHT -> "Отключена"
                        else -> "Как в системе"
                    }
                }
            }

            SectionHeader(title = stringResource(R.string.colorTheme))

            SectionContainer {
                val coroutineScope = rememberCoroutineScope()

                var colorName by remember { mutableStateOf(PrimaryColorOption.Blue.name) }

                LaunchedEffect(Unit) {
                    colorName = dataStoreManager.getPrimaryColor().firstOrNull()
                        ?: PrimaryColorOption.Blue.name
                }

                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    PrimaryColorOption.entries.forEach { option ->
                        RadioButton(
                            modifier = Modifier.scale(1.5f),
                            selected = colorName == option.name,
                            onClick = {
                                colorName = option.name
                                coroutineScope.launch {
                                    dataStoreManager.savePrimaryColor(option.name)
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
                        addScreenInStack { DarkThemeSettings() }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(backgroundColor: Color) {
    val colors = LocalCustomColors.current

    PageTopBar(
        title = { Text(stringResource(R.string.design)) },
        navigationIcon = {
            IconButton(
                onClick = {
                    removeLastScreenFromStack()
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

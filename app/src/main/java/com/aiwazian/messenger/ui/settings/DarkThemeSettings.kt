package com.aiwazian.messenger.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.aiwazian.messenger.DataStoreManager
import com.aiwazian.messenger.R
import com.aiwazian.messenger.customType.ThemeOption
import com.aiwazian.messenger.removeLastScreenFromStack
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionRadioItem
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import kotlinx.coroutines.launch

@Composable
fun DarkThemeSettings() {
    Content()
}

@Composable
private fun Content() {
    val customColors = LocalCustomColors.current

    val scrollState = rememberScrollState()

    val initialTopBarColor = customColors.secondary
    val scrolledTopBarColor = customColors.topAppBarBackground

    val topBarColor = if (scrollState.value > 0) {
        scrolledTopBarColor
    } else {
        initialTopBarColor
    }

    Scaffold(topBar = { TopBar(topBarColor) }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(customColors.secondary)
                .verticalScroll(scrollState)
        ) {
            val dataStoreManager = DataStoreManager.getInstance()
            var selectedOption by remember { mutableStateOf(ThemeOption.SYSTEM) }

            LaunchedEffect(Unit) {
                dataStoreManager.getTheme().collect { theme ->
                    selectedOption = theme
                }
            }

            val coroutine = rememberCoroutineScope()

            SectionContainer {
                SectionRadioItem(text = "Как в системе", selectedOption == ThemeOption.SYSTEM) {
                    coroutine.launch {
                        dataStoreManager.saveTheme(ThemeOption.SYSTEM)
                    }
                }

                SectionRadioItem(text = "Включена", selectedOption == ThemeOption.DARK) {
                    coroutine.launch {
                        dataStoreManager.saveTheme(ThemeOption.DARK)
                    }
                }

                SectionRadioItem(text = "Отключена", selectedOption == ThemeOption.LIGHT) {
                    coroutine.launch {
                        dataStoreManager.saveTheme(ThemeOption.LIGHT)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(backgroundColor: Color) {
    val customColors = LocalCustomColors.current

    PageTopBar(
        title = { Text(stringResource(R.string.dark_theme)) },
        navigationIcon = {
            IconButton(onClick = {
                removeLastScreenFromStack()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = customColors.text,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = customColors.text,
            containerColor = backgroundColor,
        )
    )
}
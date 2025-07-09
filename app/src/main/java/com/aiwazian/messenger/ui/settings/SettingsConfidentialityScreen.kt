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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.aiwazian.messenger.R
import com.aiwazian.messenger.addScreenInStack
import com.aiwazian.messenger.removeLastScreenFromStack
import com.aiwazian.messenger.ui.element.CustomDialog
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionDescription
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.element.SectionRadioItem
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.google.firebase.annotations.concurrent.Background

@Composable
fun SettingsConfidentialityScreen() {
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

    Scaffold(topBar = { TopBar(topBarColor) }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(colors.secondary)
                .verticalScroll(scrollState)
        ) {
            SectionHeader(stringResource(R.string.confidentiality))

            SectionContainer {
                SectionItem(
                    text = "Время захода",
                    primaryText = "Никто",
                    onClick = { addScreenInStack { TimeOfEntryScreen() } }
                )

                SectionItem(
                    text = "Сообщения",
                    primaryText = "Все",
                    onClick = { }
                )

                SectionItem(
                    text = "О себе",
                    primaryText = "Все"
                )
            }

            var showDialog = remember { mutableStateOf(false) }

            SectionHeader("Удалить мой аккаунт")

            SectionContainer {
                SectionItem(
                    text = "Если я не захожу",
                    primaryText = "12 месяцев",
                    onClick = {
                        showDialog.value = true
                    }
                )
            }

            SectionDescription("Если Вы ни разу не загляните в ${stringResource(R.string.app_name)} за это время, аккаунт будет удален.")

            val options = listOf(
                "1 месяц",
                "3 месяца",
                "6 месяцев",
                "12 месяцев"
            )

            DismissSessionFromTimeDialog(showDialog, options)
        }
    }
}

@Composable
private fun DismissSessionFromTimeDialog(showDialog: MutableState<Boolean>, options: List<String>) {
    var selectedOption by remember { mutableStateOf("Выберите пункт") }

    var selectedInDialog by remember { mutableStateOf(options[0]) }

    if (showDialog.value) {
        CustomDialog(
            title = "Удаление аккаунта при неактивности",
            onDismiss = { showDialog.value = false },
            onPrimary = {
                selectedOption = selectedInDialog
                showDialog.value = false
            }
        ) {
            options.forEach { option ->
                SectionRadioItem(
                    text = option,
                    selected = (selectedOption == option),
                    onClick = {
                        selectedOption = option
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
        title = { Text(stringResource(R.string.confidentiality)) },
        navigationIcon = {
            IconButton(onClick = {
                removeLastScreenFromStack()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.text,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = colors.text,
            containerColor = backgroundColor
        )
    )
}

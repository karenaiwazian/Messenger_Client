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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.aiwazian.messenger.R
import com.aiwazian.messenger.UserManager
import com.aiwazian.messenger.addScreenInStack
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.removeLastScreenFromStack
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionDescription
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.theme.LocalCustomColors

@Composable
fun SettingsSecurityScreen() {
    Content()
}

@Composable
private fun Content() {
    val colors = LocalCustomColors.current

    var deviceCount by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        try {
            val token = UserManager.token

            val getDevices = RetrofitInstance.api.getDeviceCount("Bearer $token")

            if (getDevices.isSuccessful) {
                deviceCount = getDevices.body() ?: 1
            }
        } catch (e: Exception) {

        }
    }

    val scrollState = rememberScrollState()

    val initialTopBarColor = colors.secondary
    val scrolledTopBarColor = colors.topAppBarBackground

    val topBarColor = if (scrollState.value > 0) {
        scrolledTopBarColor
    } else {
        initialTopBarColor
    }

    Scaffold(
        topBar = { TopBar(topBarColor) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(colors.secondary)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                SectionContainer {
                    SectionItem(
                        text = "Облачный пароль",
                        primaryText = "Выкл."
                    )

                    SectionItem(
                        text = "Код пароль",
                        primaryText = "Выкл."
                    )

                    SectionItem(
                        text = stringResource(R.string.devices),
                        primaryText = deviceCount.toString(),
                        onClick = { addScreenInStack { DeviceSettings() } }
                    )
                }

                SectionDescription(
                    text = "Просмотреть список устройств, на которых Ваш аккаунт авторизован в ${
                        stringResource(R.string.app_name)
                    }."
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
        title = { Text(stringResource(R.string.security)) },
        navigationIcon = {
            IconButton(onClick = {
                removeLastScreenFromStack()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
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

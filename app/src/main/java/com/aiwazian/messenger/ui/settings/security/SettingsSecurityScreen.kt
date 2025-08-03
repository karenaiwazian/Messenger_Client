package com.aiwazian.messenger.ui.settings.security

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.services.UserService
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.services.TokenManager
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionDescription
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.utils.DataStoreManager
import com.aiwazian.messenger.viewModels.NavigationViewModel

@Composable
fun SettingsSecurityScreen() {
    Content()
}

@Composable
private fun Content() {
    val navViewModel: NavigationViewModel = viewModel()

    var deviceCount by remember { mutableIntStateOf(1) }
    var passcodeEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val dataStoreManager = DataStoreManager.getInstance()
        dataStoreManager.getPasscode().collect {
             passcodeEnabled = it.isNotBlank()
        }

        try {
            val tokenManager = TokenManager()
            val token = tokenManager.getToken()
            val getDevices = RetrofitInstance.api.getDeviceCount(token)

            if (getDevices.isSuccessful) {
                deviceCount = getDevices.body() ?: 1
            }
        } catch (e: Exception) {
            Log.e("SettingsSecurityScreen", e.message.toString())
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { TopBar() },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column {
                SectionContainer {
                    SectionItem(
                        icon = Icons.Outlined.Key,
                        text = stringResource(R.string.cloud_password),
                        primaryText = stringResource(R.string.on),
                        onClick = {
                            navViewModel.addScreenInStack {
                                SettingsCloudPasswordScreen()
                            }
                        }
                    )

                    val passcodeEnabledText = if (passcodeEnabled) {
                        stringResource(R.string.on)
                    } else {
                        stringResource(R.string.off)
                    }

                    SectionItem(
                        icon = Icons.Outlined.Lock,
                        text = stringResource(R.string.passcode_lock),
                        primaryText = passcodeEnabledText,
                        onClick = {
                            navViewModel.addScreenInStack {
                                SettingsPasscodeScreen()
                            }
                        }
                    )

                    SectionItem(
                        icon = Icons.Outlined.Devices,
                        text = stringResource(R.string.devices),
                        primaryText = deviceCount.toString(),
                        onClick = {
                            navViewModel.addScreenInStack {
                                SettingsDevicesScreen()
                            }
                        }
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
private fun TopBar() {
    val navViewModel: NavigationViewModel = viewModel()

    PageTopBar(
        title = { Text(stringResource(R.string.security)) },
        navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    )
}

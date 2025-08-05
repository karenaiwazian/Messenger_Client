package com.aiwazian.messenger.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.LoginActivity
import com.aiwazian.messenger.R
import com.aiwazian.messenger.services.AppLockService
import com.aiwazian.messenger.services.AuthService
import com.aiwazian.messenger.services.TokenManager
import com.aiwazian.messenger.ui.element.CustomDialog
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.settings.SettingsDataUsageScreen
import com.aiwazian.messenger.ui.settings.security.SettingsPasscodeScreen
import com.aiwazian.messenger.utils.WebSocketManager
import com.aiwazian.messenger.viewModels.DialogViewModel
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun LogoutScreen() {
    Content()
}

@Composable
private fun Content() {
    val navViewModel: NavigationViewModel = viewModel()
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = { TopBar() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(scrollState)
        ) {
            SectionHeader(title = stringResource(R.string.alternative_options))
            
            SectionContainer {
                SectionItem(
                    icon = Icons.Outlined.PersonAdd,
                    text = "Добавить аккаунт",
                    description = "Подключите несколько аккаунтов и легко переключайтесь между ними."
                )
                
                SectionItem(
                    icon = Icons.Outlined.Delete,
                    text = stringResource(R.string.clear_cache),
                    description = "Освободите память устройства, файлы останутся в облаке.",
                    onClick = {
                        navViewModel.addScreenInStack {
                            SettingsDataUsageScreen()
                        }
                    })
                
                val hasPasscode = AppLockService().hasPasscode.collectAsState().value
                
                if (!hasPasscode) {
                    SectionItem(
                        icon = Icons.Outlined.Key,
                        text = "Установить код пароль",
                        description = "Включите код-пароль для разблокировки приложения на Вашем устройстве.",
                        onClick = {
                            navViewModel.addScreenInStack {
                                SettingsPasscodeScreen()
                            }
                        })
                }
            }
            
            val dialogViewModel: DialogViewModel = viewModel()
            
            SectionContainer {
                SectionItem(
                    text = stringResource(R.string.log_out),
                    textColor = MaterialTheme.colorScheme.error,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        dialogViewModel.showDialog()
                    })
            }
            
            LogoutModal(viewModel = dialogViewModel)
        }
    }
}

@Composable
private fun LogoutModal(viewModel: DialogViewModel) {
    val isVisible by viewModel.isDialogVisible
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    if (isVisible) {
        CustomDialog(
            title = stringResource(R.string.log_out),
            onDismissRequest = {
                viewModel.hideDialog()
            },
            content = {
                Text(text = "Вы точно хотите выйти?")
            },
            buttons = {
                TextButton(onClick = {
                    viewModel.hideDialog()
                }) {
                    Text(stringResource(R.string.cancel))
                }
                TextButton(onClick = {
                    scope.launch {
                        viewModel.hideDialog()
                        
                        val authService = AuthService()
                        authService.logout()
                        
                        WebSocketManager.close()
                        TokenManager.removeToken()
                        
                        val intent = Intent(
                            context,
                            LoginActivity::class.java
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                        (context as Activity).finish()
                    }
                }) {
                    Text(stringResource(R.string.log_out))
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    val navViewModel: NavigationViewModel = viewModel()
    
    PageTopBar(
        title = { Text(stringResource(R.string.log_out)) },
        navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    )
}
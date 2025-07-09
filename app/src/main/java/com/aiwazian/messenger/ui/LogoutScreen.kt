package com.aiwazian.messenger.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.AuthManager
import com.aiwazian.messenger.viewModels.DialogViewModel
import com.aiwazian.messenger.LoginActivity
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.R
import com.aiwazian.messenger.removeLastScreenFromStack
import com.aiwazian.messenger.ui.element.CustomDialog
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.theme.LocalCustomColors

@Composable
fun LogoutScreen() {
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
                .fillMaxSize()
                .padding(it)
                .background(colors.secondary)
                .verticalScroll(scrollState)
        ) {
            SectionHeader(title = "Другие возможности")

            SectionContainer {
                SectionItem(
                    icon = Icons.Outlined.PersonAdd,
                    text = "Добавить аккаунт",
                    description = "Подключите несколько аккаунтов и легко переключайтесь между ними."
                )
                SectionItem(
                    icon = Icons.Outlined.Key,
                    text = "Установить код пароль",
                    description = "Включите код-пароль для разблокировки приложения на Вашем устройстве."
                )
            }

            val dialogViewModel: DialogViewModel = viewModel()

            SectionContainer {
                SectionItem(
                    text = stringResource(R.string.exit),
                    textColor = colors.danger,
                    colors = ButtonDefaults.textButtonColors(contentColor = colors.danger),
                    onClick = {
                        dialogViewModel.showDialog()
                    }
                )
            }

            LogoutModal(viewModel = dialogViewModel)
        }
    }
}

@Composable
private fun LogoutModal(viewModel: DialogViewModel) {
    val isVisible by viewModel.isDialogVisible
    val context = LocalContext.current

    if (isVisible) {
        CustomDialog(
            title = stringResource(R.string.exit),
            onDismiss = { viewModel.hideDialog() },
            onPrimary = {
                viewModel.hideDialog()
                AuthManager.logout()
                val intent = Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            }
        ) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Вы точно хотите выйти?",
                    color = LocalCustomColors.current.text
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
        title = { Text(stringResource(R.string.exit)) },
        navigationIcon = {
            IconButton(onClick = { removeLastScreenFromStack() }) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = colors.text
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = colors.text,
            containerColor = backgroundColor
        )
    )
}
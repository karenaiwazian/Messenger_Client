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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionToggleItem
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.viewModels.NavigationViewModel

@Composable
fun SettingsNotificationsScreen() {
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

    Scaffold(
        topBar = { TopBar(topBarColor) },
        containerColor = colors.secondary,
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(colors.secondary)
                .verticalScroll(scrollState)
        ) {
            SectionHeader("Уведомления из чатов")

            SectionContainer {
                SectionToggleItem(text = "Личные чаты")

                SectionToggleItem(text = "Группы")

                SectionToggleItem(text = "Каналы")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(backgroundColor: Color) {
    val colors = LocalCustomColors.current
    val navViewModel: NavigationViewModel = viewModel()

    PageTopBar(
        title = {
            Text(stringResource(R.string.notifications))
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = colors.text,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = colors.text,
            containerColor = backgroundColor,
        )
    )
}

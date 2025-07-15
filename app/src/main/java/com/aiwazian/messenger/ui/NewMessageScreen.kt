package com.aiwazian.messenger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Group
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
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.viewModels.NavigationViewModel

@Composable
fun NewMessageScreen() {
    Content()
}

@Composable
private fun Content() {
    val navViewModel: NavigationViewModel = viewModel()

    val customColors = LocalCustomColors.current

    val scrollState = rememberScrollState()

    val initialTopBarColor = customColors.secondary
    val scrolledTopBarColor = customColors.topAppBarBackground

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
                .background(customColors.secondary)
                .verticalScroll(scrollState)
        ) {
            SectionContainer {
                SectionItem(
                    icon = Icons.Outlined.Group,
                    text = stringResource(R.string.create_group),
                    onClick = {
                        navViewModel.addScreenInStack { CreateGroupScreen() }
                    }
                )

                SectionItem(
                    icon = Icons.Outlined.Group,
                    text = stringResource(R.string.create_channel),
                    onClick = {
                        navViewModel.addScreenInStack { CreateChannelScreen() }
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
        title = { Text(stringResource(R.string.new_message)) },
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
            titleContentColor = colors.text,
            containerColor = backgroundColor
        )
    )
}

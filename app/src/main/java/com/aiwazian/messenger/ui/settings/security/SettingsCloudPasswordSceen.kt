@file:OptIn(ExperimentalMaterial3Api::class)

package com.aiwazian.messenger.ui.settings.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.viewModels.NavigationViewModel

@Composable
fun SettingsCloudPasswordScreen() {
    Content()
}

@Composable
private fun Content() {
    Scaffold(
        topBar = {
            TopBar()
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    val navViewModel: NavigationViewModel = viewModel()

    PageTopBar(
        title = {
            Text(stringResource(R.string.cloud_password))
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    )
}
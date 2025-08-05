package com.aiwazian.messenger.ui.settings.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.viewModels.NavigationViewModel

@Composable
fun SettingsChangeCloudPasswordScreen() {
    val navViewModel: NavigationViewModel = viewModel()
    Scaffold(topBar = {
        PageTopBar(navigationIcon = {
            IconButton(onClick = {
                navViewModel.removeLastScreenInStack()
            }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                //TODO change cloud password
            },
            shape = CircleShape,
            modifier = Modifier.imePadding(),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, null)
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                shape = RoundedCornerShape(10.dp),
                label = {
                    Text(stringResource(R.string.enter_password))
                }
            )
        }
    }
}
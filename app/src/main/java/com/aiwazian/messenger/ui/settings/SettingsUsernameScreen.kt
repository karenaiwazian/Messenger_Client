package com.aiwazian.messenger.ui.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.services.VibrateService
import com.aiwazian.messenger.ui.element.InputField
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.utils.VibrationPattern
import com.aiwazian.messenger.viewModels.NavigationViewModel
import com.aiwazian.messenger.viewModels.SettingsUsernameViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsUsernameScreen() {
    Content()
}

@Composable
private fun Content() {
    val navViewModel = viewModel<NavigationViewModel>()
    
    val settingsUsernameViewModel = hiltViewModel<SettingsUsernameViewModel>()
    
    val username by settingsUsernameViewModel.username.collectAsState()
    
    val errorText = settingsUsernameViewModel.errorText
    
    Scaffold(topBar = { TopBar(navViewModel::removeLastScreenInStack) }) {
        Column(modifier = Modifier.padding(it)) {
            SectionContainer {
                InputField(
                    placeholder = stringResource(R.string.username),
                    value = username,
                    onValueChange = settingsUsernameViewModel::onChangeUsername
                )
            }
            
            AnimatedContent(
                targetState = errorText,
                modifier = Modifier.padding(start = 20.dp)
            ) { text ->
                if (text != null) {
                    Text(text)
                }
            }
        }
    }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    val navViewModel = viewModel<NavigationViewModel>()
    
    val settingsUsernameViewModel = hiltViewModel<SettingsUsernameViewModel>()
    
    val canSave by settingsUsernameViewModel.canSave.collectAsState()
    
    val context = LocalContext.current
    
    val vibrationService = VibrateService(context)
    
    val scope = rememberCoroutineScope()
    
    PageTopBar(
        title = { Text(stringResource(R.string.username)) },
        navigationIcon = {
            IconButton(onClick = {
                onBack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        actions = {
            if (canSave) {
                IconButton(onClick = {
                    scope.launch {
                        val isSaved = settingsUsernameViewModel.trySave()
                        
                        if (isSaved) {
                            navViewModel.removeLastScreenInStack()
                        } else {
                            vibrationService.vibrate(VibrationPattern.Error)
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                    )
                }
            }
        })
}
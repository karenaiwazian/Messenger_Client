package com.aiwazian.messenger.ui.settings.privacy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.customType.PrivacyLevel
import com.aiwazian.messenger.services.VibrateService
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionRadioItem
import com.aiwazian.messenger.utils.VibrationPattern
import com.aiwazian.messenger.viewModels.NavigationViewModel
import com.aiwazian.messenger.viewModels.SettingsBioViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsBioScreen(
    initialLevel: PrivacyLevel,
    onChange: (PrivacyLevel) -> Unit
) {
    Content(
        initialLevel,
        onChange
    )
}

@Composable
private fun Content(
    initialLevel: PrivacyLevel,
    onChange: (PrivacyLevel) -> Unit
) {
    val navViewModel = viewModel<NavigationViewModel>()
    
    val settingsPrivacyViewModel = viewModel<SettingsBioViewModel>()
    
    val currentValue by settingsPrivacyViewModel.currentLevel.collectAsState()
    val showSaveButton by settingsPrivacyViewModel.showSaveButton.collectAsState()
    
    val scope = rememberCoroutineScope()
    
    val scrollState = rememberScrollState()
    
    val context = LocalContext.current
    
    val vibrateService = VibrateService(context)
    
    LaunchedEffect(Unit) {
        settingsPrivacyViewModel.init(initialLevel)
    }
    
    Scaffold(
        topBar = {
            PageTopBar(
                title = {
                    Text(stringResource(R.string.bio))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navViewModel.removeLastScreenInStack()
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(visible = showSaveButton) {
                        IconButton(onClick = {
                            scope.launch {
                                val isSaved = settingsPrivacyViewModel.trySave()
                                
                                if (isSaved) {
                                    onChange.invoke(currentValue)
                                    navViewModel.removeLastScreenInStack()
                                } else {
                                    vibrateService.vibrate(VibrationPattern.Error)
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null
                            )
                        }
                    }
                })
        }) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(scrollState)
        ) {
            SectionHeader("Кто видит мой раздел \"О себе\"?")
            
            SectionContainer {
                SectionRadioItem(
                    text = stringResource(R.string.everybody),
                    selected = currentValue == PrivacyLevel.Everybody,
                    onClick = {
                        settingsPrivacyViewModel.selectValue(PrivacyLevel.Everybody)
                    })
                SectionRadioItem(
                    text = stringResource(R.string.nobody),
                    selected = currentValue == PrivacyLevel.Nobody,
                    onClick = {
                        settingsPrivacyViewModel.selectValue(PrivacyLevel.Nobody)
                    })
            }
        }
    }
}

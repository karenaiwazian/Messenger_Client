package com.aiwazian.messenger.ui.settings.chat

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.customType.ThemeOption
import com.aiwazian.messenger.services.ThemeService
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionRadioItem
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch

private data class ThemeItem(
    val name: String,
    val theme: ThemeOption
)

@Composable
fun SettingsDarkThemeScreen() {
    Content()
}

@Composable
private fun Content() {
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopBar()
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(scrollState)
        ) {
            val themeService = ThemeService()
            val selectedOption by themeService.currentTheme.collectAsState()
            val themes = listOf(
                ThemeItem(
                    "Как в системе",
                    ThemeOption.SYSTEM
                ),
                ThemeItem(
                    "Включена",
                    ThemeOption.DARK
                ),
                ThemeItem(
                    "Отключена",
                    ThemeOption.LIGHT
                )
            )
            
            val coroutine = rememberCoroutineScope()
            
            SectionContainer {
                themes.forEach { (name, theme) ->
                    SectionRadioItem(
                        text = name,
                        selectedOption == theme
                    ) {
                        coroutine.launch {
                            themeService.setTheme(theme)
                        }
                    }
                    
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    val navViewModel = viewModel<NavigationViewModel>()
    
    PageTopBar(
        title = { Text(stringResource(R.string.dark_theme)) },
        navigationIcon = {
            IconButton(onClick = {
                navViewModel.removeLastScreenInStack()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        })
}
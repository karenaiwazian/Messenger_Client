package com.aiwazian.messenger.ui.settings

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.customType.Language
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionRadioItem
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.utils.LanguageService
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsLanguageScreen() {
    Content()
}

private data class LanguageItem(
    val text: String,
    val language: Language
)

@Composable
private fun Content() {
    val colors = LocalCustomColors.current
    val context = LocalContext.current

    val scrollState = rememberScrollState()

    val initialTopBarColor = colors.secondary
    val scrolledTopBarColor = colors.topAppBarBackground

    val topBarColor = if (scrollState.value > 0) {
        scrolledTopBarColor
    } else {
        initialTopBarColor
    }

    val languages = listOf(
        LanguageItem(
            text = stringResource(R.string.russian_untranslatable),
            language = Language.RU
        ),
        LanguageItem(
            text = stringResource(R.string.english_untranslatable),
            language = Language.EN
        ),
    )

    Scaffold(
        topBar = {
            TopBar(topBarColor)
        },
        containerColor = colors.secondary,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(colors.secondary)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            val languageService = LanguageService(context)
            val selectedOption = languageService.languageCode.collectAsState().value
            val scope = rememberCoroutineScope()

            SectionContainer {
                languages.forEach { item ->
                    SectionRadioItem(
                        text = item.text,
                        selected = selectedOption == item.language,
                        onClick = {
                            scope.launch {
                                if (selectedOption != item.language) {
                                    languageService.selLanguage(item.language)
                                    languageService.saveLanguage(item.language)

                                    (context as? Activity)?.recreate()
                                }
                            }
                        }
                    )
                }
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
        title = { Text(stringResource(R.string.language)) },
        navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.text
                )
            }
        },
        actions = {
            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = colors.text
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = colors.text,
            containerColor = backgroundColor
        )
    )
}

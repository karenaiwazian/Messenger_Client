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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.aiwazian.messenger.DataStoreManager
import com.aiwazian.messenger.R
import com.aiwazian.messenger.customType.Language
import com.aiwazian.messenger.removeLastScreenFromStack
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionRadioItem
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import kotlinx.coroutines.launch

@Composable
fun LanguageSettings() {
    Content()
}

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

    Scaffold(topBar = { TopBar(topBarColor) }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(colors.secondary)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            val dataStoreManager = DataStoreManager.getInstance()
            var selectedOption by remember { mutableStateOf(Language.RU) }

            LaunchedEffect(Unit) {
                dataStoreManager.getLanguage().collect {
                    selectedOption = it
                }
            }

            val coroutine = rememberCoroutineScope()

            SectionContainer {
                SectionRadioItem(
                    stringResource(R.string.russian_untranslatable),
                    selectedOption == Language.RU
                ) {
                    coroutine.launch {
                        if (selectedOption != Language.RU) {
                            dataStoreManager.saveLanguage(Language.RU)
                            (context as? Activity)?.recreate()
                        }
                    }
                }

                SectionRadioItem(
                    stringResource(R.string.english_untranslatable),
                    selectedOption == Language.EN
                ) {
                    coroutine.launch {
                        if (selectedOption != Language.EN) {
                            dataStoreManager.saveLanguage(Language.EN)
                            (context as? Activity)?.recreate()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(backgroundColor: Color) {
    val colors = LocalCustomColors.current

    PageTopBar(
        title = { Text(stringResource(R.string.language)) },
        navigationIcon = {
            IconButton(onClick = {
                removeLastScreenFromStack()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.text
                )
            }
        },
        actions = {
            IconButton(onClick = {

            }) {
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

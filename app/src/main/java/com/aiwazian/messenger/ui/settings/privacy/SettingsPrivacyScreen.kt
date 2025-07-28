package com.aiwazian.messenger.ui.settings.privacy

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.ui.element.CustomDialog
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionDescription
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.element.SectionRadioItem
import com.aiwazian.messenger.viewModels.NavigationViewModel

@Composable
fun SettingsPrivacyScreen() {
    Content()
}

@Composable
private fun Content() {
    val navViewModel: NavigationViewModel = viewModel()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { TopBar() },
        
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            SectionHeader(stringResource(R.string.confidentiality))

            SectionContainer {
                SectionItem(
                    text = stringResource(R.string.last_seen),
                    primaryText = stringResource(R.string.nobody),
                    onClick = {
                        navViewModel.addScreenInStack {
                            SettingsLastSeenScreen()
                        }
                    }
                )

                SectionItem(
                    text = stringResource(R.string.messages),
                    primaryText = stringResource(R.string.everybody),
                    onClick = { }
                )

                SectionItem(
                    text = stringResource(R.string.bio),
                    primaryText = stringResource(R.string.everybody)
                )

                SectionItem(
                    text = stringResource(R.string.date_of_birth),
                    primaryText = stringResource(R.string.everybody)
                )

                SectionItem(
                    text = stringResource(R.string.invites),
                    primaryText = stringResource(R.string.everybody)
                )
            }

            val showDialog = remember { mutableStateOf(false) }

            SectionHeader("Удалить мой аккаунт")

            SectionContainer {
                SectionItem(
                    text = "Если я не захожу",
                    primaryText = "12 месяцев",
                    onClick = {
                        showDialog.value = true
                    }
                )
            }

            SectionDescription("Если Вы ни разу не загляните в ${stringResource(R.string.app_name)} за это время, аккаунт будет удален.")

            val options = listOf(
                "1 месяц",
                "3 месяца",
                "6 месяцев",
                "12 месяцев"
            )

            DismissSessionFromTimeDialog(showDialog, options)
        }
    }
}

@Composable
private fun DismissSessionFromTimeDialog(showDialog: MutableState<Boolean>, options: List<String>) {
    var selectedOption by remember { mutableStateOf("Выберите пункт") }

    var selectedInDialog by remember { mutableStateOf(options[0]) }

    if (showDialog.value) {
        CustomDialog(
            title = "Удаление аккаунта при неактивности",
            onDismiss = { showDialog.value = false },
            onConfirm = {
                selectedOption = selectedInDialog
                showDialog.value = false
            }
        ) {
            options.forEach { option ->
                SectionRadioItem(
                    text = option,
                    selected = (selectedOption == option),
                    onClick = {
                        selectedOption = option
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    val navViewModel: NavigationViewModel = viewModel()

    PageTopBar(
        title = { Text(stringResource(R.string.confidentiality)) },
        navigationIcon = {
            IconButton(onClick = {
                navViewModel.removeLastScreenInStack()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        }
    )
}

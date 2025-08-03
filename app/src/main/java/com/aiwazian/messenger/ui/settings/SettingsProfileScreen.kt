package com.aiwazian.messenger.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aiwazian.messenger.R
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.services.UserService
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionDescription
import com.aiwazian.messenger.ui.element.SectionHeader
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.ui.element.InputField
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsProfileScreen() {
    Content()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBack: () -> Unit) {
    PageTopBar(
        title = { Text(stringResource(R.string.profile)) },
        navigationIcon = {
            IconButton(onClick = {
                onBack()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content() {
    val user by UserService.user.collectAsState()
    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var username by remember { mutableStateOf(user.username) }
    var bio by remember { mutableStateOf(user.bio) }

    val scrollState = rememberScrollState()

    val navViewModel: NavigationViewModel = viewModel()

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                onBack = {
                    val updatedUser = User(
                        firstName = firstName,
                        lastName = lastName,
                        username = username,
                        bio = bio
                    )

                    UserService.updateUser(updatedUser)
                    scope.launch {
                        UserService.saveUserData()
                    }

                    navViewModel.removeLastScreenInStack()
                }
            )
        },

        modifier = Modifier.imePadding()
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {

                SectionHeader("Ваше имя")

                SectionContainer {
                    InputField(
                        placeholder = stringResource(R.string.first_name),
                        value = firstName
                    ) {
                        firstName = it
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(start = 16.dp),
                        thickness = 1.dp,
                    )

                    InputField(
                        placeholder = stringResource(R.string.last_name),
                        value = lastName
                    ) {
                        lastName = it
                    }
                }

                SectionHeader(title = stringResource(R.string.bio))

                SectionContainer {
                    InputField("Напишите что-нибудь о себе", bio) {
                        bio = it
                    }
                }

                SectionDescription(
                    "В настройках можно выбрать, кому они будут видны.",
                )

                SectionHeader("Задать имя пользователя")

                SectionContainer {
                    InputField(
                        placeholder = stringResource(R.string.username),
                        username
                    ) { username = it }
                }

                SectionDescription(
                    "Другие пользователи смогут найти Вас по такому имени и связаться.",
                )

                SectionHeader(title = stringResource(R.string.date_of_birth))

                val openDialog = remember { mutableStateOf(false) }

                SectionContainer {
                    SectionItem(
                        text = "Дата Вашего рождения",
                        primaryText = "Указать",
                        onClick = {
                            openDialog.value = true
                        }
                    )
                }

                SectionDescription(
                    "В настройках можно выбрать, кто будет видеть Ваш день рождения.",
                )


                if (openDialog.value) {
                    val datePickerState = rememberDatePickerState()

                    DatePickerDialog(
                        onDismissRequest = {
                            openDialog.value = false
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    openDialog.value = false
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Ок")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    openDialog.value = false
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Отмена")
                            }
                        }
                    ) {
                        DatePicker(
                            state = datePickerState,
                            colors = DatePickerDefaults.colors(
                                selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                                selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                }
            }
        }
    }
}

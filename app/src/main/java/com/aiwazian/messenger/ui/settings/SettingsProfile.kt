package com.aiwazian.messenger.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.ripple
import com.aiwazian.messenger.R
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.UserManager
import com.aiwazian.messenger.removeLastScreenFromStack
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionDescription
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.TextButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.unit.sp

@Composable
fun SettingsProfile() {
    Content()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBack: () -> Unit, backgroundColor: Color) {
    val colors = LocalCustomColors.current

    PageTopBar(
        title = { Text(stringResource(R.string.profile)) },
        navigationIcon = {
            IconButton(onClick = {
                onBack()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.text,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = colors.text,
            containerColor = backgroundColor,
        )
    )
}

@Composable
private fun Content() {
    val colors = LocalCustomColors.current

    var id by remember { mutableStateOf(UserManager.user.id) }
    var firstName by remember { mutableStateOf(UserManager.user.firstName) }
    var lastName by remember { mutableStateOf(UserManager.user.lastName) }
    var username by remember { mutableStateOf(UserManager.user.username) }
    var bio by remember { mutableStateOf(UserManager.user.bio) }

    val scrollState = rememberScrollState()

    val initialTopBarColor = colors.secondary
    val scrolledTopBarColor = colors.topAppBarBackground

    val topBarColor = if (scrollState.value > 0) {
        scrolledTopBarColor
    } else {
        initialTopBarColor
    }

    Scaffold(
        topBar = {
            TopBar(
                onBack = {
                    if (!firstName.trim().isBlank()) {
                        val updatedUser = User(
                            id = id,
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            username = username.trim(),
                            bio = bio.trim()
                        )

                        UserManager.updateUser(updatedUser)
                        UserManager.saveUserData()
                    }

                    removeLastScreenFromStack()
                },
                backgroundColor = topBarColor
            )
        },
        modifier = Modifier.imePadding()
    ) {
        Box(Modifier.padding(it)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(colors.secondary)
                    .verticalScroll(scrollState)
            ) {

                SectionHeader("Ваше имя")

                SectionContainer {
                    InputField("Имя", firstName) {
                        firstName = it
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(start = 16.dp),
                        thickness = 1.dp,
                        color = colorResource(R.color.divider)
                    )

                    InputField("Фамилия", lastName) {
                        lastName = it
                    }
                }

                SectionHeader("О себе")

                SectionContainer {
                    InputField("Напишите что-нибудь о себе", bio) { bio = it }
                }

                SectionDescription(
                    "В настройках можно выбрать, кому они будут видны.",
                )

                SectionHeader("Задать имя пользователя")

                SectionContainer {
                    InputField("Имя пользователя", username) { username = it }
                }

                SectionDescription(
                    "Другие пользователи смогут найти Вас по такому имени и связаться.",
                )
            }
        }
    }
}

@Composable
fun InputField(placeholder: String, value: String, onValueChange: (String) -> Unit) {
    val customColors = LocalCustomColors.current

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        singleLine = true,
        placeholder = { Text(placeholder, color = customColors.textHint) },
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            cursorColor = customColors.primary,
            focusedTextColor = customColors.text,
            unfocusedTextColor = customColors.text,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

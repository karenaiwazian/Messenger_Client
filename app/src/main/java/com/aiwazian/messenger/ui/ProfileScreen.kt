package com.aiwazian.messenger.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aiwazian.messenger.DataStoreManager
import com.aiwazian.messenger.R
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.UserManager
import com.aiwazian.messenger.addScreenInStack
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.removeLastScreenFromStack
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.element.SectionToggleItem
import com.aiwazian.messenger.ui.settings.SettingsProfile
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import kotlinx.coroutines.flow.first

@Composable
fun ProfileScreen(userId: String) {
    if (userId == UserManager.user.id) {
        Content(UserManager.user)
        return
    }

    var userState = remember { mutableStateOf(User()) }

    LaunchedEffect(true) {
        try {
            val token = UserManager.token

            val response = RetrofitInstance.api.getUserById(token = "Bearer $token", id = userId)

            if (response.isSuccessful) {
                val getUser = response.body()
                if (getUser != null) {
                    userState.value = getUser
                    Log.d("PROFILE1", userState.toString())
                }

                Log.d("PROFILE1", userState.toString())
            } else {
                Log.d("PROFILE1", response.code().toString())
            }
        } catch (e: Exception) {

        }
    }

    Log.d("PROFILE1", userState.toString())

    Content(userState.value)
}

@Composable
private fun Content(user: User) {
    val context = LocalContext.current
    val colors = LocalCustomColors.current

    val scrollState = rememberScrollState()

    val initialTopBarColor = colors.secondary
    val scrolledTopBarColor = colors.topAppBarBackground

    val topBarColor = if (scrollState.value > 0) {
        scrolledTopBarColor
    } else {
        initialTopBarColor
    }

    Scaffold(
        topBar = { AppBar(user, topBarColor) }
    ) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
                .background(colors.secondary)
                .verticalScroll(scrollState)
        ) {
            SectionHeader(title = stringResource(R.string.information))

            SectionContainer {

                val userBio = user.bio

                if (userBio.isNotEmpty()) {
                    SectionItem(
                        text = userBio, description = "О себе",
                        onLongClick = {
                            val clipboardManager =
                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = ClipData.newPlainText("label", userBio)
                            clipboardManager.setPrimaryClip(clipData)
                        }
                    )
                }

                val username = user.username

                if (username.isNotEmpty()) {
                    SectionItem(
                        text = ("@$username"),
                        description = "Имя пользователя",
                        onLongClick = {
                            val clipboardManager =
                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = ClipData.newPlainText("label", username)
                            clipboardManager.setPrimaryClip(clipData)
                        }
                    )
                }

                if (user.id != UserManager.user.id) {
                    SectionToggleItem(text = stringResource(R.string.notifications))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(user: User, backgroundColor: Color) {
    val customColors = LocalCustomColors.current

    PageTopBar(
        title = {
            Text(
                "${user.firstName} ${user.lastName}",
                maxLines = 1,
                softWrap = false
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    removeLastScreenFromStack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = customColors.text
                )
            }
        },
        actions = {
            if (user.id == UserManager.user.id) {
                IconButton(
                    onClick = {
                        addScreenInStack { SettingsProfile() }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = customColors.text
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = customColors.text,
            containerColor = backgroundColor
        )
    )
}
package com.aiwazian.messenger.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.QrCode
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.utils.ClipboardHelper
import com.aiwazian.messenger.R
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.utils.UserManager
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.element.SectionToggleItem
import com.aiwazian.messenger.ui.settings.SettingsProfileScreen
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.viewModels.NavigationViewModel

@Composable
fun ProfileScreen(userId: String) {
    if (userId == UserManager.user.id) {
        Content(UserManager.user)
        return
    }

    val userState = remember { mutableStateOf(User()) }

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
            Log.e("PROFILE1", e.message.toString())
        }
    }

    Log.d("PROFILE1", userState.toString())

    Content(userState.value)
}

@Composable
private fun Content(user: User) {
    val navViewModel: NavigationViewModel = viewModel()
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

    val clipboardHelper = ClipboardHelper(context = context)

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
                        text = userBio,
                        description = stringResource(R.string.bio),
                        onLongClick = {
                            clipboardHelper.copy("userBio")
                        }
                    )
                }

                val username = user.username

                if (username.isNotEmpty()) {
                    SectionItem(
                        text = ("@$username"),
                        description = stringResource(R.string.username),
                        onLongClick = {
                            clipboardHelper.copy(username)
                        },
                        primaryIcon = Icons.Outlined.QrCode,
                        primaryIconClick = {
                            navViewModel.addScreenInStack { QRCodeScreen() }
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
    val navViewModel: NavigationViewModel = viewModel()
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
                    navViewModel.removeLastScreenInStack()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = customColors.text
                )
            }
        },
        actions = {
            if (user.id == UserManager.user.id) {
                IconButton(
                    onClick = {
                        navViewModel.addScreenInStack { SettingsProfileScreen() }
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
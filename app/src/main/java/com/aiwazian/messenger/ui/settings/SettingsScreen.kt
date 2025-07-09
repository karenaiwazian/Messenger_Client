package com.aiwazian.messenger.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.DataUsage
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aiwazian.messenger.DataStoreManager
import com.aiwazian.messenger.R
import com.aiwazian.messenger.addScreenInStack
import com.aiwazian.messenger.customType.Language
import com.aiwazian.messenger.removeLastScreenFromStack
import com.aiwazian.messenger.ui.LogoutScreen
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionDescription
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.theme.LocalCustomColors

@Composable
fun SettingsScreen() {
    Content()
}

@Composable
private fun Content() {
    val customColors = LocalCustomColors.current

    val dataStoreManager = DataStoreManager.getInstance()
    var language by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        dataStoreManager.getLanguage().collect {
            language = when (it) {
                Language.RU -> "Русский"
                else -> "English"
            }
        }
    }

    val scrollState = rememberScrollState()

    val initialTopBarColor = customColors.secondary
    val scrolledTopBarColor = customColors.topAppBarBackground

    val topBarColor = if(scrollState.value > 0) {
        scrolledTopBarColor
    } else {
        initialTopBarColor
    }

    Scaffold(
        topBar = { TopBar(topBarColor) },
        containerColor = customColors.secondary,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            SectionHeader(stringResource(R.string.account))

            SectionContainer {
                SectionItem(
                    text = stringResource(R.string.profile),
                    description = stringResource(R.string.aboutMe),
                    onClick = { addScreenInStack { SettingsProfile() } }
                )

                SectionItem(
                    text = stringResource(R.string.security),
                    description = stringResource(R.string.protectYourAccount),
                    onClick = { addScreenInStack { SettingsSecurityScreen() } }
                )
            }

            SectionHeader(stringResource(R.string.settings))

            SectionContainer {
                SectionItem(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    text = stringResource(R.string.design),
                    onClick = { addScreenInStack { ChatSettings() } }
                )

                SectionItem(
                    icon = Icons.Outlined.Notifications,
                    text = stringResource(R.string.notifications),
                    onClick = { addScreenInStack { SettingsNotificationsScreen() } }
                )

                SectionItem(
                    icon = Icons.Outlined.Lock,
                    text = stringResource(R.string.confidentiality),
                    onClick = { addScreenInStack { SettingsConfidentialityScreen() } }
                )

                SectionItem(
                    icon = Icons.Outlined.DataUsage,
                    text = stringResource(R.string.dataAndStorage),
                    onClick = { addScreenInStack { SettingsDataUsageScreen() } }
                )

                SectionItem(
                    icon = Icons.Outlined.Folder,
                    text = stringResource(R.string.chatFolders),
                    onClick = { addScreenInStack { ChatFoldersSettings() } }
                )

                SectionItem(
                    icon = Icons.Outlined.Language,
                    text = stringResource(R.string.language),
                    primaryText = language,
                    onClick = { addScreenInStack { LanguageSettings() } }
                )
            }

            SectionHeader(stringResource(R.string.help))

            SectionContainer {
                SectionItem(
                    icon = Icons.Outlined.QuestionMark,
                    text = stringResource(R.string.faq),
                )

                SectionItem(
                    icon = Icons.Outlined.Shield,
                    text = stringResource(R.string.privacyPolicy),
                )
            }

            val context = LocalContext.current

            val packageInfo = remember {
                context.packageManager.getPackageInfo(context.packageName, 0)
            }

            val versionName = packageInfo.versionName
            val versionCode = packageInfo.longVersionCode

            SectionDescription(text = "${stringResource(R.string.app_name)} v${versionName} (${versionCode})")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(backgroundColor: Color) {
    val customColors = LocalCustomColors.current
    var menuExpanded by remember { mutableStateOf(false) }

    PageTopBar(
        title = { Text(stringResource(R.string.settings)) },
        navigationIcon = {
            IconButton(onClick = {
                removeLastScreenFromStack()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = customColors.text,
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = customColors.text
                    )
                }
                DropdownMenu(
                    modifier = Modifier.background(customColors.background),
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = stringResource(R.string.exit),
                                tint = customColors.textHint
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.exit),
                                color = customColors.text
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            addScreenInStack { LogoutScreen() }
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = customColors.text
        )
    )
}

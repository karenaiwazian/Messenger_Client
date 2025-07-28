package com.aiwazian.messenger.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.DataUsage
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MoreVert
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.customType.Language
import com.aiwazian.messenger.ui.LogoutScreen
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionDescription
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.settings.chat.SettingsChatScreen
import com.aiwazian.messenger.ui.settings.privacy.SettingsPrivacyScreen
import com.aiwazian.messenger.ui.settings.security.SettingsSecurityScreen
import com.aiwazian.messenger.utils.LanguageService
import com.aiwazian.messenger.viewModels.NavigationViewModel

@Composable
fun SettingsScreen() {
    Content()
}

@Composable
private fun Content() {
    val navViewModel: NavigationViewModel = viewModel()

    val languageService = LanguageService(LocalContext.current)
    val selectedLanguage = when (languageService.languageCode.collectAsState().value) {
        Language.RU -> stringResource(R.string.russian_untranslatable)
        Language.EN -> stringResource(R.string.english_untranslatable)
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopBar()
        },
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
                    description = stringResource(R.string.write_about_me),
                    onClick = {
                        navViewModel.addScreenInStack { SettingsProfileScreen() }
                    })

                SectionItem(
                    text = stringResource(R.string.security),
                    description = stringResource(R.string.protect_your_account),
                    onClick = {
                        navViewModel.addScreenInStack { SettingsSecurityScreen() }
                    })
            }

            SectionHeader(stringResource(R.string.settings))

            SectionContainer {
                SectionItem(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    text = stringResource(R.string.design),
                    onClick = {
                        navViewModel.addScreenInStack { SettingsChatScreen() }
                    })

                SectionItem(
                    icon = Icons.Outlined.Lock,
                    text = stringResource(R.string.confidentiality),
                    onClick = {
                        navViewModel.addScreenInStack { SettingsPrivacyScreen() }
                    })

                SectionItem(
                    icon = Icons.Outlined.Notifications,
                    text = stringResource(R.string.notifications),
                    onClick = {
                        navViewModel.addScreenInStack { SettingsNotificationsScreen() }
                    })

                SectionItem(
                    icon = Icons.Outlined.DataUsage,
                    text = stringResource(R.string.data_and_storage),
                    onClick = {
                        navViewModel.addScreenInStack { SettingsDataUsageScreen() }
                    })

                SectionItem(
                    icon = Icons.Outlined.Folder,
                    text = stringResource(R.string.chat_folders),
                    onClick = {
                        navViewModel.addScreenInStack { SettingsChatFoldersScreen() }
                    })

                SectionItem(
                    icon = Icons.Outlined.Language,
                    text = stringResource(R.string.language),
                    primaryText = selectedLanguage,
                    onClick = {
                        navViewModel.addScreenInStack { SettingsLanguageScreen() }
                    })
            }

            SectionHeader(stringResource(R.string.help))

            SectionContainer {
                SectionItem(
                    icon = Icons.Outlined.QuestionMark,
                    text = stringResource(R.string.faq),
                )

                SectionItem(
                    icon = Icons.Outlined.Shield,
                    text = stringResource(R.string.privacy_policy),
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
private fun TopBar() {
    val navViewModel: NavigationViewModel = viewModel()
    var menuExpanded by remember { mutableStateOf(false) }

    PageTopBar(
        title = { Text(stringResource(R.string.settings)) }, navigationIcon = {
            IconButton(onClick = {
                navViewModel.removeLastScreenInStack()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        }, actions = {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = null,
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(leadingIcon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.exit),
                        )
                    }, text = {
                        Text(
                            text = stringResource(R.string.exit)
                        )
                    }, onClick = {
                        menuExpanded = false
                        navViewModel.addScreenInStack {
                            LogoutScreen()
                        }
                    })
                }
            }
        }
    )
}

package com.aiwazian.messenger.ui.settings.chat

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DisabledVisible
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.customType.PrimaryColorOption
import com.aiwazian.messenger.customType.ThemeOption
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionDescription
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.element.SectionToggleItem
import com.aiwazian.messenger.services.ThemeService
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsChatScreen() {
    Content()
}

@Composable
private fun Content() {
    val navViewModel: NavigationViewModel = viewModel()

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopBar()
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            val themeService = ThemeService()
            val primaryColor by themeService.primaryColor.collectAsState()
            val theme = when (themeService.currentTheme.collectAsState().value) {
                ThemeOption.DARK -> "Включена"
                ThemeOption.LIGHT -> "Отключена"
                else -> "Как в системе"
            }

            SectionHeader(title = stringResource(R.string.color_theme))

            SectionContainer {
                val coroutineScope = rememberCoroutineScope()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val isDynamicColorEnable by themeService.dynamicColor.collectAsState()

                    SectionToggleItem(
                        text = stringResource(R.string.dynamic_color),
                        isChecked = isDynamicColorEnable,
                        onCheckedChange = {
                            coroutineScope.launch {
                                themeService.setDynamicColor(!isDynamicColorEnable)
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    PrimaryColorOption.entries.forEach { option ->
                        RadioButton(
                            modifier = Modifier.scale(1.5f),
                            selected = primaryColor == option,
                            onClick = {
                                coroutineScope.launch {
                                    themeService.setPrimaryColor(option)
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = option.color,
                                unselectedColor = option.color,
                            )
                        )
                    }
                }
            }

            SectionContainer {
                SectionItem(
                    text = stringResource(R.string.dark_theme), primaryText = theme, onClick = {
                        navViewModel.addScreenInStack {
                            SettingsDarkThemeScreen()
                        }
                    })
            }

            SectionHeader(title = "Список чатов")

            SectionContainer {

            }

            var vds by remember { mutableStateOf("") }

            var selected by remember { mutableIntStateOf(0) }

            SectionHeader(title = "Смахивание влево в списке чатов $vds")

            SectionContainer {
                Row(
                    modifier = Modifier
                        .padding(5.dp)
                        .horizontalScroll(rememberScrollState())
                ) {

                    Boxic(
                        selected = selected == 1, icon = Icons.Outlined.Archive, onClick = {
                            vds = "Архивировать"
                            selected = 1
                        })

                    Boxic(
                        selected = selected == 2, icon = Icons.Outlined.PushPin, onClick = {
                            vds = "Закрепить"
                            selected = 2
                        })

                    Boxic(
                        selected = selected == 5, icon = Icons.Outlined.ChatBubble, onClick = {
                            vds = "Прочитать"
                            selected = 5
                        })

                    Boxic(
                        selected = selected == 6,
                        icon = Icons.AutoMirrored.Outlined.VolumeOff,
                        onClick = {
                            vds = "Выкл. звук"
                            selected = 6
                        })

                    Boxic(
                        selected = selected == 3, icon = Icons.Outlined.DisabledVisible, onClick = {
                            vds = "Сменить папку"
                            selected = 3
                        })

                    Boxic(
                        selected = selected == 4, icon = Icons.Outlined.Delete, onClick = {
                            vds = "Удалить"
                            selected = 4
                        })
                }
            }

            SectionDescription(text = "Выбор действия, которое будет выполняться при смахивании влево в списке чатов.")
        }
    }
}

@Composable
private fun Boxic(
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onClick()
            }) {
        Box(
            modifier = Modifier
            //.padding(4.dp)
            //.clip(RoundedCornerShape(10.dp))
            //.background(back)
        ) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    val navViewModel: NavigationViewModel = viewModel()

    PageTopBar(title = {
        Text(stringResource(R.string.design))
    }, navigationIcon = {
        IconButton(
            onClick = {
                navViewModel.removeLastScreenInStack()
            }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
            )
        }
    })
}

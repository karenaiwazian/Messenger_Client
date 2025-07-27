package com.aiwazian.messenger.ui.settings.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.outlined.VolumeOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
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
import androidx.compose.ui.graphics.Color
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
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.utils.ThemeService
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsChatScreen() {
    Content()
}

@Composable
private fun Content() {
    val navViewModel: NavigationViewModel = viewModel()
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
        topBar = {
            TopBar(topBarColor)
        }, containerColor = colors.secondary
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.secondary)
                .verticalScroll(scrollState)
        ) {
            val themeService = ThemeService()
            val primaryColor = themeService.primaryColor.collectAsState().value
            val theme = when (themeService.currentTheme.collectAsState().value) {
                ThemeOption.DARK -> "Включена"
                ThemeOption.LIGHT -> "Отключена"
                else -> "Как в системе"
            }

            SectionHeader(title = stringResource(R.string.color_theme))

            SectionContainer {
                val coroutineScope = rememberCoroutineScope()

                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    PrimaryColorOption.entries.forEach { option ->
                        RadioButton(
                            modifier = Modifier.scale(1.5f),
                            selected = primaryColor == option,
                            onClick = {
                                coroutineScope.launch {
                                    themeService.changePrimaryColor(option)
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
                        selected = selected == 1,
                        icon = Icons.Outlined.Archive,
                        onClick = {
                            vds = "Архивировать"
                            selected = 1
                        })

                    Boxic(
                        selected = selected == 2,
                        icon = Icons.Outlined.PushPin,
                        onClick = {
                            vds = "Закрепить"
                            selected = 2
                        })

                    Boxic(
                        selected = selected == 5,
                        icon = Icons.Outlined.ChatBubble,
                        onClick = {
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
                        selected = selected == 3,
                        icon = Icons.Outlined.DisabledVisible,
                        backgroundColor = colors.textHint,
                        onClick = {
                            vds = "Сменить папку"
                            selected = 3
                        })

                    Boxic(
                        selected = selected == 4,
                        icon = Icons.Outlined.Delete,
                        backgroundColor = colors.dangerBackground,
                        onClick = {
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
    backgroundColor: Color = LocalCustomColors.current.primary
) {
    val colors = LocalCustomColors.current

    val back by animateColorAsState(
        targetValue = if (!selected) colors.background else backgroundColor
    )

    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onClick()
            }
            .background(back)) {
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
                    .background(backgroundColor)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(backgroundColor: Color) {
    val navViewModel: NavigationViewModel = viewModel()
    val colors = LocalCustomColors.current

    PageTopBar(
        title = {
            Text(stringResource(R.string.design))
        }, navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.text,
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor, titleContentColor = colors.text
        )
    )
}

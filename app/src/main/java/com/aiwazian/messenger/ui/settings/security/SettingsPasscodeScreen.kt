package com.aiwazian.messenger.ui.settings.security

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aiwazian.messenger.utils.JsonAnimation
import com.aiwazian.messenger.R
import com.aiwazian.messenger.ui.element.CodeBlocks
import com.aiwazian.messenger.ui.element.CustomDialog
import com.aiwazian.messenger.ui.element.CustomNumberBoard
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.element.SectionToggleItem
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.utils.DataStoreManager
import com.aiwazian.messenger.viewModels.DialogViewModel
import com.aiwazian.messenger.viewModels.NavigationViewModel
import com.aiwazian.messenger.viewModels.PasscodeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private object PasscodeScreens {
    const val Main = "PasscodeMain"
    const val Settings = "PasscodeSettings"
    const val Create = "CreatePasscode"
    const val Change = "ChangePasscode"
}

@Composable
fun SettingsPasscodeScreen() {
    val navViewModel: NavigationViewModel = viewModel()
    val navController = rememberNavController()

    val transition = tween<IntOffset>(
        durationMillis = 500, easing = FastOutSlowInEasing
    )

    var startDestination by remember { mutableStateOf(PasscodeScreens.Main) }

    LaunchedEffect(Unit) {
        val dataStore = DataStoreManager.getInstance()

        startDestination = if (dataStore.getPasscode().first().isNotBlank()) {
            PasscodeScreens.Settings
        } else {
            PasscodeScreens.Main
        }
    }

    NavHost(navController = navController, startDestination = startDestination, enterTransition = {
        slideInHorizontally(animationSpec = transition) { it }
    }, exitTransition = {
        slideOutHorizontally(animationSpec = transition) { -it }
    }, popEnterTransition = {
        slideInHorizontally(animationSpec = transition) { -it }
    }, popExitTransition = {
        slideOutHorizontally(animationSpec = transition) { it }
    }) {
        composable(route = PasscodeScreens.Main) {
            PasscodeLockMainScreen(navController, navViewModel)
        }
        composable(route = PasscodeScreens.Create) {
            CreatePasscodeScreen(navController, navViewModel)
        }
        composable(route = PasscodeScreens.Settings) {
            SettingsPasscodeLockScreen(navController, navViewModel)
        }
        composable(route = PasscodeScreens.Change) {
            SettingsChangePasscodeLockScreen(navController)
        }
    }
}

@Composable
private fun CreatePasscodeScreen(
    navController: NavHostController, navViewModel: NavigationViewModel
) {
    val colors = LocalCustomColors.current

    val passcodeViewModel: PasscodeViewModel = viewModel()

    passcodeViewModel.onSaveNewPasscode = {
        navController.navigate(route = PasscodeScreens.Settings) {
            popUpTo(PasscodeScreens.Main) {
                inclusive = true
            }
            popUpTo(PasscodeScreens.Create) {
                inclusive = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(navViewModel)
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.secondary
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(40.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Text(text = "Создание код-пароля", color = colors.text, fontSize = 24.sp)

                    Text(
                        text = "Введите 4 цифры, которые хотите использовать для разблокировки приложения.",
                        textAlign = TextAlign.Center,
                        color = colors.textHint,
                        fontSize = 14.sp,
                        lineHeight = 16.sp
                    )
                }

                CodeBlocks(
                    count = PasscodeViewModel.MAX_LENGTH_PASSCODE,
                    showInput = false,
                    code = passcodeViewModel.passcode
                )
            }

            val boardButtons = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf(null, "0", Icons.AutoMirrored.Outlined.Backspace),
            )

            CustomNumberBoard(
                value = passcodeViewModel.passcode,
                buttons = boardButtons,
                onChange = passcodeViewModel::onPasscodeChanged
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsChangePasscodeLockScreen(
    navController: NavHostController
) {
    val colors = LocalCustomColors.current

    val passcodeViewModel: PasscodeViewModel = viewModel()

    passcodeViewModel.onSaveNewPasscode = {
        navController.navigate(route = PasscodeScreens.Settings) {
            popUpTo(PasscodeScreens.Main) {
                inclusive = true
            }
            popUpTo(PasscodeScreens.Create) {
                inclusive = true
            }
        }
    }

    Scaffold(
        topBar = {
            PageTopBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = colors.text,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = colors.text,
                    containerColor = colors.secondary,
                )
            )
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.secondary
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(40.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Text(text = "Введите новый код-пароль", color = colors.text, fontSize = 24.sp)

                    Text(
                        text = "Введите 4 цифры, которые хотите использовать для разблокировки приложения.",
                        textAlign = TextAlign.Center,
                        color = colors.textHint,
                        fontSize = 14.sp,
                        lineHeight = 16.sp
                    )
                }

                CodeBlocks(
                    count = PasscodeViewModel.MAX_LENGTH_PASSCODE, code = passcodeViewModel.passcode
                )
            }

            val boardButtons = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf(null, "0", Icons.AutoMirrored.Outlined.Backspace),
            )

            CustomNumberBoard(
                value = passcodeViewModel.passcode,
                buttons = boardButtons,
                onChange = passcodeViewModel::onPasscodeChanged
            )
        }
    }
}

@Composable
private fun PasscodeLockMainScreen(
    navController: NavHostController, navViewModel: NavigationViewModel
) {
    val colors = LocalCustomColors.current

    Scaffold(
        topBar = {
            TopBar(navViewModel)
        }, containerColor = colors.secondary
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val composition by rememberLottieComposition(
                    spec = LottieCompositionSpec.Asset(JsonAnimation.Key_Lock)
                )

                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.size(100.dp),
                    iterations = LottieConstants.IterateForever,
                    isPlaying = true
                )

                Text(
                    text = stringResource(R.string.passcode_lock),
                    color = colors.text,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                Text(
                    text = "После установки кода-пароля над списком чатов появится значок замка для блокировки и разблокировки приложения.",
                    textAlign = TextAlign.Center,
                    color = colors.textHint,
                    fontSize = 14.sp,
                    lineHeight = 16.sp
                )
            }

            Button(
                onClick = {
                    navController.navigate(route = PasscodeScreens.Create) {
                        popUpTo(PasscodeScreens.Main) {
                            inclusive = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = colors.primary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.enable_passcode),
                    modifier = Modifier.padding(8.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(navigationViewModel: NavigationViewModel) {
    val colors = LocalCustomColors.current

    PageTopBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = {
                    navigationViewModel.removeLastScreenInStack()
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = colors.text,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = colors.text,
            containerColor = colors.secondary,
        )
    )
}

@Composable
private fun SettingsPasscodeLockScreen(
    navController: NavHostController,
    navViewModel: NavigationViewModel
) {
    val colors = LocalCustomColors.current
    val passcodeViewModel: PasscodeViewModel = viewModel()

    val dialogViewModel: DialogViewModel = viewModel()

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
            TopBar(topBarColor, navViewModel)
        }, containerColor = colors.secondary
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val composition by rememberLottieComposition(
                    spec = LottieCompositionSpec.Asset(JsonAnimation.Key_Lock)
                )

                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.size(100.dp),
                    iterations = LottieConstants.IterateForever,
                    isPlaying = true
                )

                Text(
                    text = "Для блокировки и разблокировки приложения нажмите на значок замка над списком чатов.",
                    color = colors.text,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center
                )

            }

            SectionContainer {
                SectionItem(text = "Сменить код-пароль", onClick = {
                    navController.navigate(PasscodeScreens.Create)
                })
            }

            SectionContainer {
                SectionItem(
                    text = "Выключить код-пароль",
                    textColor = colors.danger,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colors.danger
                    ),
                    onClick = {
                        dialogViewModel.showDialog()
                    })
            }

            val scope = rememberCoroutineScope()

            if (dialogViewModel.isDialogVisible.value) {
                DisablePasscodeDialog(onDismiss = {
                    dialogViewModel.hideDialog()
                }, onConfirm = {
                    scope.launch {
                        passcodeViewModel.disablePasscode()
                        dialogViewModel.hideDialog()
                        navViewModel.removeLastScreenInStack()
                    }
                })
            }
        }
    }
}

@Composable
private fun DisablePasscodeDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    CustomDialog(
        title = "Выключить код пароль?", onDismiss = onDismiss, onConfirm = onConfirm
    ) {
        Text(
            text = "Вы точно хотите отключить пароль?",
            modifier = Modifier.padding(horizontal = 16.dp),
            color = LocalCustomColors.current.text
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(backgroundColor: Color, navViewModel: NavigationViewModel) {
    val colors = LocalCustomColors.current

    PageTopBar(
        title = { Text(stringResource(R.string.passcode_lock)) }, navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = colors.text,
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = colors.text,
            containerColor = backgroundColor,
        )
    )
}
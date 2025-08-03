package com.aiwazian.messenger.ui.settings.security

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aiwazian.messenger.R
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.Session
import com.aiwazian.messenger.ui.element.BottomModalSheet
import com.aiwazian.messenger.ui.element.CustomDialog
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionDescription
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.element.SectionRadioItem
import com.aiwazian.messenger.services.DeviceHelper
import com.aiwazian.messenger.services.TokenManager
import com.aiwazian.messenger.utils.LottieAnimation
import com.aiwazian.messenger.services.UserService
import com.aiwazian.messenger.viewModels.DeviceSettingsViewModel
import com.aiwazian.messenger.viewModels.DialogViewModel
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsDevicesScreen() {
    Content()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content() {
    val viewModel: DeviceSettingsViewModel = viewModel()

    var sessions by remember { mutableStateOf<List<Session>>(emptyList()) }

    var triggerForReloadUserSessions by remember { mutableStateOf(false) }

    LaunchedEffect(triggerForReloadUserSessions) {
        try {
            val tokenManager = TokenManager()
            val token = tokenManager.getToken()
            val getDevices = RetrofitInstance.api.getSessions(token)

            if (getDevices.isSuccessful) {
                sessions = getDevices.body() ?: emptyList()
            }
        } catch (e: Exception) {
            sessions = emptyList()
            Log.e("Error", e.message.toString())
        }
    }

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    val scrollState = rememberScrollState()

    val navViewModel: NavigationViewModel = viewModel()

    Scaffold(
        topBar = {
            PageTopBar(
                title = { Text(stringResource(R.string.devices)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navViewModel.removeLastScreenInStack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            )
        },

        snackbarHost = {
            SwipeDismissSnackbarHost(snackbarHostState)
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            val deviceHelper = DeviceHelper()
            val currentDeviceName = deviceHelper.getDeviceName()

            var modalSessionDeviceName by remember { mutableStateOf("") }
            var modalSessionCreatedTime by remember { mutableStateOf("") }
            var onDismissClick by remember { mutableStateOf<(() -> Unit)?>(null) }

            val dismissSessionViewModel: DialogViewModel = viewModel()

            val bottomSheetController = remember { DialogViewModel() }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val composition by rememberLottieComposition(
                    spec = LottieCompositionSpec.Asset(LottieAnimation.APPLE_PHONE)
                )

                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.size(100.dp),
                    iterations = LottieConstants.IterateForever,
                    isPlaying = true
                )

                Text(
                    text = "Вы можете зайти в приложение с помощью QR-кода.",
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QrCode,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = "Подключить устройство",
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }

            SectionHeader(stringResource(R.string.this_device))

            SectionContainer {
                DeviceCard(
                    text = currentDeviceName,
                    onClick = {
                        onDismissClick = null
                        modalSessionDeviceName = currentDeviceName
                        modalSessionCreatedTime = "В сети"

                        bottomSheetController.showDialog()
                    }
                )

            }

            if (sessions.isNotEmpty()) {
                SectionContainer {
                    SectionItem(
                        icon = Icons.Outlined.BackHand,
                        iconColor = MaterialTheme.colorScheme.error,
                        text = "Завершить все другие сеансы",
                        textColor = MaterialTheme.colorScheme.error,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        onClick = {
                            onDismissClick = {
                                scope.launch {
                                    viewModel.terminateAllOtherSessions(
                                        success = {
                                            triggerForReloadUserSessions =
                                                !triggerForReloadUserSessions
                                        },
                                        fail = {

                                        }
                                    )
                                }
                            }

                            dismissSessionViewModel.showDialog()
                        }
                    )
                }
            }

            if (sessions.isNotEmpty()) {
                SectionDescription(text = "Выйти на всех устройствах, кроме этого.")
            }

            SectionHeader("Автоматически завершать сеансы")

            val dismissSessionFromTimeDialogViewModel =
                remember { DialogViewModel() }

            SectionContainer {
                SectionItem(
                    text = "Если сеанс неактивен",
                    primaryText = "1 нед.",
                    onClick = {
                        dismissSessionFromTimeDialogViewModel.showDialog()
                    }
                )
            }

            if (sessions.isNotEmpty()) {
                SectionHeader(title = "Активные сеансы")

                SectionContainer {
                    Column {
                        sessions.forEach { session ->
                            DeviceCard(
                                text = session.deviceName, onClick = {
                                    modalSessionDeviceName = session.deviceName
                                    modalSessionCreatedTime = session.createdAt

                                    bottomSheetController.showDialog()

                                    onDismissClick = {
                                        // TODO(Dismiss session)
                                        triggerForReloadUserSessions = !triggerForReloadUserSessions

                                        bottomSheetController.hideDialog()
                                    }
                                }
                            )
                        }
                    }
                }
            }

            val options = listOf("1 неделя", "1 месяц", "1 год")

            DismissSessionFromTimeDialog(dismissSessionFromTimeDialogViewModel, options)

            DismissSessionDialog(
                viewModel = dismissSessionViewModel,
                dismiss = {
                    bottomSheetController.hideDialog()
                    onDismissClick?.invoke()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Сессия завершена",
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            )

            BottomModalSheet(
                viewModel = bottomSheetController,
                dragHandle = null
            ) {
                SectionContainer {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Color.Green), contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_launcher_foreground),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = modalSessionDeviceName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W500
                            )

                            Text(
                                text = modalSessionCreatedTime
                            )
                        }

                        if (onDismissClick != null) {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                                onClick = {
                                    dismissSessionViewModel.showDialog()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text(
                                    text = "Завершить сеанс",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SwipeDismissSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(hostState = snackbarHostState) { data ->
        var dismissed by remember { mutableStateOf(false) }

        if (!dismissed) {
            val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd
                }
            )

            SwipeToDismissBox(
                state = swipeToDismissBoxState,
                enableDismissFromEndToStart = true,
                enableDismissFromStartToEnd = true,
                backgroundContent = { }
            ) {
                Snackbar(
                    modifier = Modifier
                        .padding(12.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Text(data.visuals.message)
                }
            }
        }
    }
}

@Composable
private fun DeviceCard(text: String, onClick: () -> Unit) {
    Card(
        shape = RectangleShape,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = text, modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
private fun DismissSessionDialog(viewModel: DialogViewModel, dismiss: () -> Unit) {
    val isVisible by viewModel.isDialogVisible

    if (isVisible) {
        CustomDialog(
            title = "Завершить сеанс", onDismiss = { viewModel.hideDialog() }, onConfirm = dismiss
        ) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text("Вы точно хотите завершить сеанс?")
            }
        }
    }
}

@Composable
private fun DismissSessionFromTimeDialog(
    viewModel: DialogViewModel,
    options: List<String>,
) {
    val isVisible by viewModel.isDialogVisible

    var selectedOption by remember { mutableStateOf("Выберите пункт") }

    var selectedInDialog by remember { mutableStateOf(options[0]) }

    if (isVisible) {
        CustomDialog(
            title = "Автозавершение сессий",
            onDismiss = { viewModel.hideDialog() },
            onConfirm = {
                selectedOption = selectedInDialog
                viewModel.hideDialog()
            }
        ) {
            options.forEach { option ->
                SectionRadioItem(
                    text = option, selected = (selectedOption == option), onClick = {
                        selectedOption = option
                    }
                )
            }
        }
    }
}

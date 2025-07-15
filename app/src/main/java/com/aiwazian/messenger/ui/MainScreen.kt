package com.aiwazian.messenger.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.utils.UserManager
import com.aiwazian.messenger.utils.VibrateService
import com.aiwazian.messenger.utils.WebSocketManager
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.settings.SettingsScreen
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.utils.DataStoreManager
import com.aiwazian.messenger.utils.VibrationPattern
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    MainScreenContent()
}

@Composable
private fun MainScreenContent() {
    val context = LocalContext.current

    val showPermissionRationale = remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            showPermissionRationale.value = true
        }
    }

    if (showPermissionRationale.value) {
        NotificationBottomModal(enable = {
            showPermissionRationale.value = false

            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(
                    Settings.EXTRA_APP_PACKAGE, context.packageName
                )
            }

            context.startActivity(intent)
        }, disable = {
            showPermissionRationale.value = false
        })
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(drawerState, scope)
        },
    ) {
        Content(drawerState, scope)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationBottomModal(enable: () -> Unit, disable: () -> Unit) {
    val colors = LocalCustomColors.current

    ModalBottomSheet(
        onDismissRequest = disable,
        dragHandle = null,
        containerColor = colors.background,
        contentColor = colors.text
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(colors.primary)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    tint = Color.White
                )
            }

            Column {
                Text(
                    text = "Включите уведомления",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W500,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                Text(
                    text = "Разрешите приложению отправлять Вам уведомления, чтобы не пропустить сообщения от друзей и родных.",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = enable,
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary
            )
        ) {
            Text(
                text = "Открыть настройки", modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun Content(
    drawerState: DrawerState,
    scope: CoroutineScope,
) {
    val customColors = LocalCustomColors.current
    val navViewModel: NavigationViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(drawerState, scope)
        },
        snackbarHost = {
            SwipeDismissSnackbarHost(snackbarHostState)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                val userChats = remember { mutableStateListOf<User>() }

                var triggerLoadUserChats by remember { mutableStateOf(false) }

                LaunchedEffect(triggerLoadUserChats) {
                    val token = UserManager.token

                    try {
                        val response = RetrofitInstance.api.getContacts("Bearer $token")

                        if (response.isSuccessful) {
                            userChats.addAll(response.body() ?: emptyList())
                        }
                    } catch (e: Exception) {
                        Log.e("MainScreen", "Content: ${e.message}")
                    }
                }

                WebSocketManager.onMessage = {
                    triggerLoadUserChats = !triggerLoadUserChats
                }

                LazyColumn {
                    items(userChats) { chat ->
                        var chatName = "${chat.firstName} ${chat.lastName}"

                        if (chat.id == UserManager.user.id) {
                            chatName = stringResource(R.string.saved_messages)
                        }

                        ChatCard(
                            chatName = chatName,
                            lastMessage = "s",
                            chatId = chat.id,
                            onDismiss = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Чат в архиве",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            })
                    }
                }

                if (userChats.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Чтобы начать общение нажмите на значок поиска в правом верхнем углу и найдите пользователя по его @username",
                            textAlign = TextAlign.Center,
                            color = customColors.text
                        )
                    }
                }
            }
        },
        containerColor = customColors.secondary,
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    navViewModel.addScreenInStack {
                        NewMessageScreen()
                    }
                },
                containerColor = customColors.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeDismissSnackbarHost(snackbarHostState: SnackbarHostState) {
    val colors = LocalCustomColors.current

    SnackbarHost(
        modifier = Modifier.fillMaxWidth(),
        hostState = snackbarHostState
    ) { data ->
        var dismissed by remember { mutableStateOf(false) }

        if (!dismissed) {
            val swipeToDismissBoxState = rememberSwipeToDismissBoxState()

            SwipeToDismissBox(
                state = swipeToDismissBoxState,
                backgroundContent = { },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.background),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Archive,
                                contentDescription = null,
                                tint = colors.text
                            )

                            Text(
                                text = data.visuals.message,
                                color = colors.text
                            )
                        }

                        TextButton(
                            onClick = { },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = colors.primary,
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Reply,
                                contentDescription = null,
                                tint = colors.primary
                            )

                            Text(
                                text = "ОТМЕНА",
                                color = colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(drawerState: DrawerState, scope: CoroutineScope) {
    val customColors = LocalCustomColors.current
    val isConnected by WebSocketManager.isConnectedState.collectAsState()
    val navViewModel: NavigationViewModel = viewModel()
    val dataStoreManager = DataStoreManager.getInstance()

    var showLockIcon by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        dataStoreManager.getPasscode().collect {
            showLockIcon = it.isNotBlank()
        }
    }

    PageTopBar(
        title = {
            Text(text = if (isConnected) stringResource(R.string.app_name) else "Соединение...")
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = customColors.text
                )
            }
        },
        actions = {
            if (showLockIcon) {
                IconButton(
                    onClick = {
                        scope.launch {
                            dataStoreManager.saveIsLockApp(true)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LockOpen,
                        contentDescription = null,
                        tint = customColors.text
                    )
                }
            }

            IconButton(
                onClick = {
                    navViewModel.addScreenInStack {
                        SearchScreen()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = customColors.text
                )
            }
        },
    )
}

@Composable
private fun DrawerContent(
    drawerState: DrawerState, scope: CoroutineScope,
) {
    val navViewModel: NavigationViewModel = viewModel()
    val customColors = LocalCustomColors.current
    val user = UserManager.user

    ModalDrawerSheet(
        drawerContainerColor = customColors.background,
        drawerShape = RectangleShape,
        modifier = Modifier.fillMaxWidth(0.7f)
    ) {

        Text(
            text = "${user.firstName} ${user.lastName}",
            modifier = Modifier.padding(
                start = 20.dp,
                top = 80.dp,
                end = 20.dp,
                bottom = 40.dp
            ),
            fontSize = 24.sp,
            maxLines = 1,
            softWrap = false,
            color = customColors.text
        )

        DrawerItem(
            label = stringResource(R.string.profile), icon = Icons.Outlined.AccountCircle
        ) {
            scope.launch {
                drawerState.close()
            }
            navViewModel.addScreenInStack {
                ProfileScreen(user.id)
            }
        }

        DrawerItem(
            label = stringResource(R.string.saved_messages),
            icon = Icons.Outlined.BookmarkBorder
        ) {
            scope.launch {
                drawerState.close()
            }
            navViewModel.addScreenInStack {
                ChatScreen(user.id)
            }
        }

        DrawerItem(
            label = stringResource(R.string.settings), icon = Icons.Outlined.Settings
        ) {
            scope.launch {
                drawerState.close()
            }
            navViewModel.addScreenInStack {
                SettingsScreen()
            }
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    NavigationDrawerItem(
        shape = RectangleShape, label = {
            Text(text = label, color = customColors.text)
        }, icon = {
            Icon(
                imageVector = icon, contentDescription = null, tint = customColors.textHint
            )
        }, selected = false, onClick = onClick
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ChatCard(
    chatName: String,
    lastMessage: String,
    chatId: String,
    onDismiss: () -> Unit
) {
    val navViewModel: NavigationViewModel = viewModel()

    val context = LocalContext.current
    val colors = LocalCustomColors.current

    val openChat = {
        navViewModel.addScreenInStack {
            ChatScreen(chatId)
        }
    }

    var deleted by remember { mutableStateOf(false) }

    var isBigVibrateTriggered by remember { mutableStateOf(false) }

    val dismissDirection = SwipeToDismissBoxValue.EndToStart

    val scope = rememberCoroutineScope()

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == dismissDirection) {
                scope.launch {
                    delay(500)
                    if (!deleted) {
                        deleted = true
                        onDismiss()
                    }
                }
            }
            it != SwipeToDismissBoxValue.StartToEnd
        }
    )

    val swipedValueForDelete = 0.5f

    val backgroundColor by animateColorAsState(
        targetValue = if (swipeToDismissBoxState.progress > swipedValueForDelete) {
            colors.textHint
        } else {
            colors.primary
        }
    )

    val vibrateService = VibrateService(context)

    LaunchedEffect(swipeToDismissBoxState.progress) {
        val isRightDirection = swipeToDismissBoxState.dismissDirection == dismissDirection

        if (isRightDirection && swipeToDismissBoxState.progress > swipedValueForDelete) {
            if (!isBigVibrateTriggered) {
                vibrateService.vibrate(VibrationPattern.TactileResponse)
                isBigVibrateTriggered = true
            }
        } else {
            if (isBigVibrateTriggered) {
                vibrateService.vibrate(VibrationPattern.TactileResponse)
                isBigVibrateTriggered = false
            }
        }
    }

    if (!deleted) {
        SwipeToDismissBox(
            enableDismissFromStartToEnd = false,
            state = swipeToDismissBoxState,
            backgroundContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(backgroundColor)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Archive,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        ) {
            Card(
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.background),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                onClick = openChat
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(chatName, color = colors.text, fontSize = 20.sp)
//                  Text(lastMessage, color = colors.textHint)
                }
            }
        }
    }
}

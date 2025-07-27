package com.aiwazian.messenger.ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.ui.element.ChatCard
import com.aiwazian.messenger.utils.UserManager
import com.aiwazian.messenger.utils.WebSocketManager
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SwipeableChatCard
import com.aiwazian.messenger.ui.settings.SettingsScreen
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.utils.AppLockService
import com.aiwazian.messenger.viewModels.ChatsViewModel
import com.aiwazian.messenger.viewModels.NavigationViewModel

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
                    context, POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(drawerState)
        },
    ) {
        Content(drawerState)
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
    drawerState: DrawerState
) {
    val customColors = LocalCustomColors.current
    val navViewModel: NavigationViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    val chatsViewModel: ChatsViewModel = viewModel()

    val chatList by chatsViewModel.unarchivedChats.collectAsState()
    val archiveChats by chatsViewModel.archivedChats.collectAsState()
    val selectedChats by chatsViewModel.selectedChats.collectAsState()

    var trigger by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    WebSocketManager.onConnect = {
        scope.launch {
            trigger = true
        }
    }

    LaunchedEffect(trigger) {
        trigger = false
        chatsViewModel.loadUnarchiveChats()
        chatsViewModel.loadArchiveChats()
    }

    BackHandler(selectedChats.isNotEmpty()) {
        chatsViewModel.unselectAllChats()
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        DefaultTopBar(drawerState)

        AnimatedVisibility(
            visible = selectedChats.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(durationMillis = 100)),
            exit = fadeOut(animationSpec = tween(durationMillis = 100))
        ) {
            SelectedChatTopBar(onBack = {
                chatsViewModel.unselectAllChats()
            }, selectedCount = selectedChats.size, onClickArchive = {
                selectedChats.forEach { selectedChat ->
                    scope.launch {
                        chatsViewModel.archiveChat(selectedChat)
                    }
                }
                chatsViewModel.unselectAllChats()
            }, onClickPin = {
                selectedChats.forEach { selectedChat ->
                    scope.launch {
                        chatsViewModel.pinChat(selectedChat)
                    }
                }
            })
        }
    }, snackbarHost = {
        SwipeDismissSnackbarHost(snackbarHostState)
    }, content = { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            val onMessageHandler: (Message) -> Unit = { message: Message ->
                scope.launch {
                    val hasChat = chatList.firstOrNull { it.id == message.senderId }
                        ?: archiveChats.firstOrNull { it.id == message.senderId }

                    if (hasChat == null) {
                        chatsViewModel.loadUnarchiveChats()
                    } else {
                        chatsViewModel.moveToUp(message.receiverId)
                    }
                }
            }

            LaunchedEffect(Unit) {
                WebSocketManager.onReceiveMessage = onMessageHandler
                WebSocketManager.onSendMessage = onMessageHandler
            }

            if (archiveChats.isNotEmpty()) {
                ChatCard(
                    chatName = stringResource(R.string.archive),
                    lastMessage = "",
                    onClickChat = {
                        navViewModel.addScreenInStack {
                            ArchiveScreen()
                        }
                    }
                )
            }

            if (chatList.isEmpty() && archiveChats.isEmpty()) {
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

            LazyColumn {
                items(chatList) { chat ->
                    var chatName = chat.chatName

                    if (chat.id == UserManager.user.id) {
                        chatName = stringResource(R.string.saved_messages)
                    }

                    SwipeableChatCard(
                        chatName = chatName,
                        lastMessage = "",
                        selected = chat.id in selectedChats,
                        pinned = chat.isPinned,
                        enableSwipeabale = selectedChats.isEmpty(),
                        onClick = {
                            if (selectedChats.isEmpty()) {
                                navViewModel.addScreenInStack {
                                    ChatScreen(userId = chat.id)
                                }
                            } else {
                                chatsViewModel.selectChat(chat.id)
                            }
                        },
                        onLongClick = {
                            chatsViewModel.selectChat(chat.id)
                        },
                        backgroundIcon = Icons.Outlined.Archive,
                        onDismiss = {
                            scope.launch {
                                chatsViewModel.archiveChat(chat.id)

                                val result = snackbarHostState.showSnackbar(
                                    message = "Чат в архиве",
                                    actionLabel = "Отменить",
                                    duration = SnackbarDuration.Short
                                )

                                if (result == SnackbarResult.ActionPerformed) {
                                    chatsViewModel.unarchiveChat(chat.id)
                                }
                            }
                        })
                }
            }
        }
    }, containerColor = customColors.secondary, floatingActionButton = {
        FloatingActionButton(
            shape = CircleShape, onClick = {
                navViewModel.addScreenInStack {
                    NewMessageScreen()
                }
            }, containerColor = customColors.primary
        ) {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = null,
                tint = Color.White
            )
        }
    })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeDismissSnackbarHost(snackbarHostState: SnackbarHostState) {
    val colors = LocalCustomColors.current

    SnackbarHost(
        hostState = snackbarHostState, modifier = Modifier.fillMaxWidth()
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
                                text = data.visuals.message, color = colors.text
                            )
                        }

                        TextButton(
                            onClick = { data.performAction() },
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
                                text = "ОТМЕНА", color = colors.primary
                            )
                        }
                    }
                }
            }
        } else {
            data.dismiss()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectedChatTopBar(
    onBack: () -> Unit, selectedCount: Int, onClickArchive: () -> Unit, onClickPin: () -> Unit
) {
    val colors = LocalCustomColors.current
    var expanded by remember { mutableStateOf(false) }

    PageTopBar(title = {
        AnimatedContent(
            targetState = selectedCount, transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically(animationSpec = tween(durationMillis = 200)) { height -> height } + fadeIn(
                        animationSpec = tween(durationMillis = 200)
                    ) togetherWith slideOutVertically(animationSpec = tween(durationMillis = 200)) { height -> -height } + fadeOut(
                        animationSpec = tween(durationMillis = 200)
                    )
                } else {
                    slideInVertically(animationSpec = tween(durationMillis = 200)) { height -> -height } + fadeIn(
                        animationSpec = tween(durationMillis = 200)
                    ) togetherWith slideOutVertically(animationSpec = tween(durationMillis = 200)) { height -> height } + fadeOut(
                        animationSpec = tween(durationMillis = 200)
                    )
                }
            }) { count ->
            Text(text = count.toString())
        }
    }, navigationIcon = {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                tint = colors.text
            )
        }
    }, actions = {
        IconButton(onClick = onClickArchive) {
            Icon(
                imageVector = Icons.Outlined.Archive,
                contentDescription = null,
                tint = colors.text
            )
        }
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = colors.text
            )
        }
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = null,
                tint = colors.text
            )
        }

        DropdownMenu(
            modifier = Modifier.background(colors.background),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = {
                Text(text = "Закрепить", color = colors.text)
            }, onClick = onClickPin, leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.PushPin,
                    contentDescription = null,
                    tint = colors.text
                )
            })
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTopBar(drawerState: DrawerState) {
    val colors = LocalCustomColors.current
    val isConnected by WebSocketManager.isConnectedState.collectAsState()
    val navViewModel: NavigationViewModel = viewModel()
    val scope = rememberCoroutineScope()

    val appLockService = AppLockService()
    val showLockIcon = appLockService.passcode.collectAsState().value.isNotBlank()

    PageTopBar(
        title = {
            AnimatedContent(
                targetState = isConnected, transitionSpec = {
                    if (isConnected) {
                        slideInVertically(animationSpec = tween(durationMillis = 200)) { height -> height } + fadeIn(
                            animationSpec = tween(durationMillis = 200)
                        ) togetherWith slideOutVertically(animationSpec = tween(durationMillis = 200)) { height -> -height } + fadeOut(
                            animationSpec = tween(durationMillis = 200)
                        )
                    } else {
                        slideInVertically(animationSpec = tween(durationMillis = 200)) { height -> -height } + fadeIn(
                            animationSpec = tween(durationMillis = 200)
                        ) togetherWith slideOutVertically(animationSpec = tween(durationMillis = 200)) { height -> height } + fadeOut(
                            animationSpec = tween(durationMillis = 200)
                        )
                    }
                }) { connected ->
                Text(
                    text = if (connected) {
                        stringResource(R.string.app_name)
                    } else {
                        stringResource(R.string.connecting) + "..."
                    }
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }) {
                Icon(
                    imageVector = Icons.Default.Menu, contentDescription = null, tint = colors.text
                )
            }
        },
        actions = {
            if (showLockIcon) {
                IconButton(
                    onClick = {
                        scope.launch {
                            appLockService.lockApp()
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Outlined.LockOpen,
                        contentDescription = null,
                        tint = colors.text
                    )
                }
            }

            IconButton(
                onClick = {
                    navViewModel.addScreenInStack {
                        SearchScreen()
                    }
                }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = colors.text
                )
            }
        },
    )
}

@Composable
private fun DrawerContent(
    drawerState: DrawerState
) {
    val navViewModel: NavigationViewModel = viewModel()
    val customColors = LocalCustomColors.current
    val user = UserManager.user
    val scope = rememberCoroutineScope()

    ModalDrawerSheet(
        drawerContainerColor = customColors.background,
        drawerShape = RectangleShape,
        modifier = Modifier.fillMaxWidth(0.7f)
    ) {

        Text(
            text = "${user.firstName} ${user.lastName}", modifier = Modifier.padding(
                start = 20.dp, top = 80.dp, end = 20.dp, bottom = 40.dp
            ), fontSize = 24.sp, maxLines = 1, softWrap = false, color = customColors.text
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
            label = stringResource(R.string.saved_messages), icon = Icons.Outlined.BookmarkBorder
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
    label: String, icon: ImageVector, onClick: () -> Unit
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

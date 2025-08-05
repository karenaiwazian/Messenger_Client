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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Tab
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aiwazian.messenger.R
import com.aiwazian.messenger.data.ChatFolder
import com.aiwazian.messenger.data.ChatInfo
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.ui.element.ChatCard
import com.aiwazian.messenger.services.UserService
import com.aiwazian.messenger.utils.WebSocketManager
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SwipeableChatCard
import com.aiwazian.messenger.ui.settings.SettingsScreen
import com.aiwazian.messenger.services.AppLockService
import com.aiwazian.messenger.utils.LottieAnimation
import com.aiwazian.messenger.viewModels.ChatsViewModel
import com.aiwazian.messenger.viewModels.FolderViewModel
import com.aiwazian.messenger.viewModels.NavigationViewModel
import java.util.Date

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
    ModalBottomSheet(
        onDismissRequest = disable,
        dragHandle = null,
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
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
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
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Открыть настройки", modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    drawerState: DrawerState
) {
    val navViewModel: NavigationViewModel = viewModel()

    val chatsViewModel: ChatsViewModel = viewModel()
    val folderViewModel: FolderViewModel = viewModel()

    val chatList by chatsViewModel.unarchivedChats.collectAsState()
    val archiveChats by chatsViewModel.archivedChats.collectAsState()
    val selectedChats by chatsViewModel.selectedChats.collectAsState()
    val allSelectedChatsArePinned by chatsViewModel.allSelectedArePinned.collectAsState()
    val folders by folderViewModel.folders.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var hasChat by remember { mutableStateOf(false) }

    val onMessageHandler: (Message) -> Unit = { message ->
        hasChat = chatList.firstOrNull { it.id == message.chatId } != null

        folders.forEach { folder ->
            folder.chats.forEach {
                if (it.id == message.chatId) {
                    hasChat = true
                }
            }
        }

        if (hasChat) {
            chatsViewModel.updateLastMessage(message.chatId, message)
            chatsViewModel.moveToUp(message.chatId)
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        stringResource(R.string.all_chats),
        *folders.map { it.folderName }.toTypedArray()
    )

    val pagerState = rememberPagerState {
        tabs.size
    }

    WebSocketManager.onReceiveMessage = onMessageHandler

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    LaunchedEffect(Unit) {
        folderViewModel.loadFolders()
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
            SelectedChatTopBar(
                onBack = {
                    chatsViewModel.unselectAllChats()
                }, selectedCount = selectedChats.size, onClickArchive = {
                    scope.launch {
                        selectedChats.forEach { selectedChat ->
                            chatsViewModel.archiveChat(selectedChat)
                        }
                    }
                    chatsViewModel.unselectAllChats()
                }, dropdownActions = arrayOf(
                    SelectedChatsAction(
                        icon = Icons.Outlined.PushPin,
                        title = if (allSelectedChatsArePinned) stringResource(R.string.unpin) else stringResource(
                            R.string.pin
                        ),
                        onClick = {
                            scope.launch {
                                if (allSelectedChatsArePinned) {
                                    selectedChats.forEach { selectedChat ->
                                        chatsViewModel.unpinChat(selectedChat)
                                    }
                                } else {
                                    selectedChats.forEach { selectedChat ->
                                        chatsViewModel.pinChat(selectedChat)
                                    }
                                }
                            }
                            chatsViewModel.unselectAllChats()
                        })
                )
            )
        }
    }, snackbarHost = {
        SwipeDismissSnackbarHost(snackbarHostState)
    }, floatingActionButton = {
        FloatingButton(onClick = {
            navViewModel.addScreenInStack {
                NewMessageScreen()
            }
        })
    }) { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            AnimatedVisibility(
                visible = folders.isNotEmpty(),
                enter = slideInVertically(tween(100)),
                exit = slideOutVertically(tween(100))
            ) {
                PrimaryScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 8.dp,
                    divider = { }
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = {
                                selectedTabIndex = index
                            },
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 10.dp,
                                        topEnd = 10.dp
                                    )
                                ),
                            text = {
                                Text(text = tab)
                            },
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = folders.isNotEmpty() && selectedChats.isEmpty(),
                modifier = Modifier.fillMaxSize()
            ) { index ->
                val chatFolders = folders.toMutableList()

                val allChatFolder = ChatFolder(
                    id = 0, folderName = stringResource(R.string.all_chats), chats = chatList
                )

                chatFolders.add(0, allChatFolder)

                val folder = chatFolders[index]

                Column(modifier = Modifier.fillMaxSize()) {
                    if (folder.id == 0) {
                        if (folder.chats.isEmpty() && archiveChats.isEmpty()) {
                            EmptyChatPlaceholder(text = "Чтобы начать общение нажмите на значок поиска в правом верхнем углу и найдите пользователя по его @username")
                            return@Column
                        }

                        if (archiveChats.isNotEmpty()) {
                            ChatCard(
                                chatName = stringResource(R.string.archive),
                                onClickChat = {
                                    if (selectedChats.isEmpty()) {
                                        navViewModel.addScreenInStack {
                                            ArchiveScreen()
                                        }
                                    }
                                }
                            )
                        }
                    }

                    if (folder.id != 0 && folder.chats.isEmpty()) {
                        EmptyChatPlaceholder(
                            text = "Добавьте чаты в папку",
                            animation = LottieAnimation.FOLDER_CLOSED
                        )
                        return@Column
                    }

                    ChatListSection(
                        chatList = folder.chats,
                        selectedChats = selectedChats,
                        onChatClick = { chat ->
                            if (selectedChats.isEmpty()) {
                                navViewModel.addScreenInStack {
                                    ChatScreen(userId = chat.id)
                                }
                            } else {
                                chatsViewModel.selectChat(chat.id)
                            }
                        },
                        onChatLongClick = { chat ->
                            chatsViewModel.selectChat(chat.id)
                        },
                        onChatSwipe = { chat ->
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
    }
}

@Composable
private fun ChatListSection(
    chatList: List<ChatInfo>,
    selectedChats: List<Int>,
    onChatClick: (ChatInfo) -> Unit,
    onChatLongClick: (ChatInfo) -> Unit,
    onChatSwipe: (ChatInfo) -> Unit
) {
    val user by UserService.user.collectAsState()

    LazyColumn {
        items(chatList, key = { it.id }) { chat ->
            var chatName = chat.chatName
            if (chat.id == user.id) {
                chatName = stringResource(R.string.saved_messages)
            }
            SwipeableChatCard(
                chatName = chatName,
                lastMessage = chat.lastMessage,
                selected = chat.id in selectedChats,
                pinned = chat.isPinned,
                enableSwipeable = selectedChats.isEmpty(),
                onClick = { onChatClick(chat) },
                onLongClick = { onChatLongClick(chat) },
                backgroundIcon = Icons.Outlined.Archive,
                onDismiss = { onChatSwipe(chat) })
        }
    }
}

@Composable
private fun EmptyChatPlaceholder(text: String, animation: String? = null) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (animation != null) {
            val composition by rememberLottieComposition(
                spec = LottieCompositionSpec.Asset(animation)
            )

            LottieAnimation(
                composition = composition,
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 10.dp),
                iterations = LottieConstants.IterateForever,
                isPlaying = true
            )
        }

        Text(
            text = text,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FloatingButton(onClick: () -> Unit) {
    FloatingActionButton(
        shape = CircleShape,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            imageVector = Icons.Default.Create, contentDescription = null
        )
    }
}

@Composable
private fun SwipeDismissSnackbarHost(snackbarHostState: SnackbarHostState) {
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
                        .background(MaterialTheme.colorScheme.surface),
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
                            )

                            Text(
                                text = data.visuals.message
                            )
                        }

                        TextButton(
                            onClick = { data.performAction() },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Reply,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "ОТМЕНА", color = MaterialTheme.colorScheme.primary
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

private data class SelectedChatsAction(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectedChatTopBar(
    onBack: () -> Unit,
    selectedCount: Int,
    onClickArchive: () -> Unit,
    vararg dropdownActions: SelectedChatsAction
) {
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
            )
        }
    }, actions = {
        IconButton(onClick = onClickArchive) {
            Icon(
                imageVector = Icons.Outlined.Archive,
                contentDescription = null,
            )
        }
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
            )
        }
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = null,
            )
        }

        DropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }) {
            dropdownActions.forEach { action ->
                DropdownMenuItem(text = {
                    Text(text = action.title)
                }, onClick = action.onClick, leadingIcon = {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                    )
                })
            }
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTopBar(drawerState: DrawerState) {
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
                        slideInVertically(tween(200)) { height -> height } + fadeIn(
                            tween(200)
                        ) togetherWith slideOutVertically(tween(200)) { height -> -height } + fadeOut(
                            tween(200)
                        )
                    } else {
                        slideInVertically(tween(200)) { height -> -height } + fadeIn(
                            tween(200)
                        ) togetherWith slideOutVertically(tween(200)) { height -> height } + fadeOut(
                            tween(200)
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
                    imageVector = Icons.Default.Menu,
                    contentDescription = null
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
    val user by UserService.user.collectAsState()
    val scope = rememberCoroutineScope()

    ModalDrawerSheet(
        drawerShape = RectangleShape, modifier = Modifier.fillMaxWidth(0.7f)
    ) {

        Text(
            text = "${user.firstName} ${user.lastName}", modifier = Modifier.padding(
                start = 20.dp, top = 80.dp, end = 20.dp, bottom = 40.dp
            ), fontSize = 24.sp, maxLines = 1, softWrap = false
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
    NavigationDrawerItem(
        shape = RectangleShape, label = {
            Text(text = label)
        }, icon = {
            Icon(
                imageVector = icon, contentDescription = null
            )
        }, selected = false, onClick = onClick
    )
}

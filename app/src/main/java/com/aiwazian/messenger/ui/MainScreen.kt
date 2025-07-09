package com.aiwazian.messenger.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import com.aiwazian.messenger.R
import com.aiwazian.messenger.UserManager
import com.aiwazian.messenger.VibrateManager
import com.aiwazian.messenger.WebSocketManager
import com.aiwazian.messenger.addScreenInStack
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.settings.SettingsScreen
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val colors = LocalCustomColors.current

    val showPermissionRationale = remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            showPermissionRationale.value = true
        }
    }

    if (showPermissionRationale.value) {
        NotificationBottomModal(
            enable = {
                showPermissionRationale.value = false

                val intent =
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(
                            Settings.EXTRA_APP_PACKAGE,
                            context.packageName
                        )
                    }

                context.startActivity(intent)
            },
            disable = {
                showPermissionRationale.value = false
            }
        )
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
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
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
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
            Text(text = "Открыть настройки", modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
private fun Content(
    drawerState: DrawerState,
    scope: CoroutineScope,
) {
    val customColors = LocalCustomColors.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(drawerState, scope)
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
                            chatId = chat.id
                        )
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
                onClick = { addScreenInStack { NewMessageScreen() } },
                containerColor = customColors.primary
            ) {
                Icon(
                    Icons.Default.Create,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(drawerState: DrawerState, scope: CoroutineScope) {
    val customColors = LocalCustomColors.current

    val isConnected by WebSocketManager.isConnectedState.collectAsState()

    PageTopBar(
        title = {
            Text(text = if (isConnected) stringResource(R.string.app_name) else "Соединение...")
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch { drawerState.open() }
            }) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null,
                    tint = customColors.text
                )
            }
        },
        actions = {
            IconButton(onClick = {
                addScreenInStack(screen = { SearchScreen() })
            }) {
                Icon(
                    Icons.Default.Search,
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
    val customColors = LocalCustomColors.current
    val user = UserManager.user

    ModalDrawerSheet(
        drawerContainerColor = customColors.background,
        drawerShape = RectangleShape,
        modifier = Modifier.fillMaxWidth(0.7f)
    ) {

        Text(
            text = "${user.firstName} ${user.lastName}",
            modifier = Modifier.padding(start = 20.dp, top = 80.dp, end = 20.dp, bottom = 40.dp),
            fontSize = 24.sp,
            maxLines = 1,
            softWrap = false,
            color = customColors.text
        )

        DrawerItem(
            label = stringResource(R.string.profile),
            icon = Icons.Outlined.AccountCircle
        ) {
            scope.launch { drawerState.close() }
            addScreenInStack { ProfileScreen(user.id) }
        }

        DrawerItem(
            label = stringResource(R.string.saved_messages),
            icon = Icons.Outlined.BookmarkBorder
        ) {
            scope.launch { drawerState.close() }
            addScreenInStack { ChatScreen(user.id) }
        }

        DrawerItem(
            label = stringResource(R.string.settings),
            icon = Icons.Outlined.Settings
        ) {
            scope.launch { drawerState.close() }
            addScreenInStack { SettingsScreen() }
        }
    }
}

@Composable
private fun DrawerItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    val customColors = LocalCustomColors.current

    NavigationDrawerItem(
        shape = RectangleShape,
        label = { Text(label, color = customColors.text) },
        icon = { Icon(icon, contentDescription = null, tint = customColors.textHint) },
        selected = false,
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ChatCard(chatName: String, lastMessage: String, chatId: String) {
    val openChat = {
        addScreenInStack { ChatScreen(chatId) }
    }

    val context = LocalContext.current

    val colors = LocalCustomColors.current

    var dismissed by remember { mutableStateOf(false) }

    var deleted by remember { mutableStateOf(false) }

    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                dismissed = true
                true
            } else false
        }
    )

    val swipedValueForDelete = 0.5f

    val backgroundColor by animateColorAsState(
        targetValue = if (dismissState.progress.fraction > swipedValueForDelete) colors.textHint else colors.primary
    )

    var idBigVibrate by remember { mutableStateOf(false) }

    val vibrateManager = VibrateManager()

    LaunchedEffect(dismissState.progress.fraction) {
        if (dismissState.dismissDirection == DismissDirection.EndToStart && dismissState.progress.fraction > swipedValueForDelete) {
            if (!idBigVibrate) {
                vibrateManager.vibrate(context, longArrayOf(0, 50))
                idBigVibrate = true
            }
        } else {
            if (idBigVibrate) {
                vibrateManager.vibrate(context, longArrayOf(0, 50))
                idBigVibrate = false
            }
        }
    }

    LaunchedEffect(dismissed) {
        if (dismissed) {
            delay(500)
            deleted = true
        }
    }

    if (!deleted) {
        SwipeToDismiss(
            state = dismissState,
            dismissThresholds = {
                FractionalThreshold(0.5f)
            },
            background = {
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
                        Icons.Outlined.Archive,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },
            directions = setOf(DismissDirection.EndToStart)
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

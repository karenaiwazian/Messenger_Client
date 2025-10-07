package com.aiwazian.messenger.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.data.ChannelInfo
import com.aiwazian.messenger.data.ChatInfo
import com.aiwazian.messenger.data.DropdownMenuAction
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.data.NavigationIcon
import com.aiwazian.messenger.data.TopBarAction
import com.aiwazian.messenger.enums.ChatType
import com.aiwazian.messenger.services.ClipboardHelper
import com.aiwazian.messenger.services.UserManager
import com.aiwazian.messenger.ui.element.CustomDialog
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.utils.Shape
import com.aiwazian.messenger.utils.WebSocketManager
import com.aiwazian.messenger.viewModels.ChannelViewModel
import com.aiwazian.messenger.viewModels.ChatViewModel
import com.aiwazian.messenger.viewModels.MainViewModel
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import java.time.format.TextStyle as MonthTextStyle

@Composable
fun ChatScreen(
    chatId: Int,
    chatType: ChatType
) {
    Content(
        chatId,
        chatType
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
private fun Content(
    chatId: Int,
    chatType: ChatType
) {
    val context = LocalContext.current
    
    val clipboardHelper = ClipboardHelper(context)
    
    val navViewModel = viewModel<NavigationViewModel>()
    
    val mainViewModel = hiltViewModel<MainViewModel>()
    
    val chatViewModel = hiltViewModel<ChatViewModel>()
    
    val chatInfo by chatViewModel.chatInfo.collectAsState()
    
    val selectedMessages by chatViewModel.selectedMessages.collectAsState()
    
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        chatViewModel.open(chatId)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            chatViewModel.close()
        }
    }
    
    val profile by chatViewModel.profile.collectAsState()
    
    val deleteChatDialog = chatViewModel.deleteChatDialog
    
    val clearHistoryChatDialog = chatViewModel.clearHistoryDialog
    
    val deleteMessageDialog = chatViewModel.deleteMessageDialog
    
    val messageText by chatViewModel.messageText.collectAsState()
    
    val messages by chatViewModel.messages.collectAsState()
    
    var subTitle by remember { mutableStateOf("") }
    
    val listState = rememberLazyListState()
    
    var isVisibleLeaveDialog by remember { mutableStateOf(false) }
    
    val actions = when (chatType) {
        ChatType.PRIVATE -> {
            subTitle = ""
            
            arrayOf(
                TopBarAction(
                    icon = Icons.Outlined.MoreVert,
                    dropdownActions = arrayOf(
                        //                        DropdownMenuAction(
                        //                            icon = Icons.Outlined.CleaningServices,
                        //                            text = stringResource(R.string.clear_history),
                        //                            onClick = clearHistoryChatDialog::show
                        //                        ),
                        DropdownMenuAction(
                            icon = Icons.Outlined.DeleteOutline,
                            text = stringResource(R.string.delete_chat),
                            onClick = deleteChatDialog::show
                        )
                    )
                )
            )
        }
        
        ChatType.CHANNEL -> {
            if (profile is ChannelInfo) {
                subTitle =
                    "${(profile as ChannelInfo).subscribers} ${stringResource(R.string.subscribers).lowercase()}"
            }
            
            if (profile is ChannelInfo && (profile as ChannelInfo).ownerId != chatViewModel.myId) {
                arrayOf(
                    TopBarAction(
                        icon = Icons.Outlined.MoreVert,
                        dropdownActions = arrayOf(
                            DropdownMenuAction(
                                icon = Icons.AutoMirrored.Outlined.Logout,
                                text = stringResource(R.string.leave_channel),
                                onClick = {
                                    isVisibleLeaveDialog = true
                                })
                        )
                    )
                )
            } else {
                emptyArray()
            }
        }
        
        else -> {
            emptyArray()
        }
    }
    
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(index = messages.lastIndex)
        }
    }
    
    Scaffold(
        topBar = {
            TopBar(
                chatInfo,
                subTitle,
                actions
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyChatPlaceholder()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Bottom,
                    overscrollEffect = rememberOverscrollEffect()
                ) {
                    itemsIndexed(
                        items = messages,
                        key = { _, message -> message.id }) { index, message ->
                        val currentMessageSendDate = Instant.ofEpochMilli(message.sendTime)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        
                        val monthName = currentMessageSendDate.month.getDisplayName(
                            MonthTextStyle.FULL,
                            Locale.getDefault()
                        )
                        
                        val capitalizedMonthName = monthName.replaceFirstChar {
                            if (it.isLowerCase()) {
                                it.titlecase(Locale.getDefault())
                            } else {
                                it.toString()
                            }
                        }
                        
                        val showDateSeparator = if (index > 0) {
                            val previousMessageSendDate =
                                Instant.ofEpochMilli(messages[index - 1].sendTime)
                                    .atZone(ZoneId.systemDefault()).toLocalDate()
                            !currentMessageSendDate.isEqual(previousMessageSendDate)
                        } else {
                            true
                        }
                        
                        if (showDateSeparator) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier.clip(CircleShape)
                                ) {
                                    Text(
                                        text = "${currentMessageSendDate.dayOfMonth} $capitalizedMonthName",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        
                        MessageBubble(
                            message = message,
                            onDelete = {
                                deleteMessageDialog.show()
                                chatViewModel.selectMessage(message)
                            },
                            onEdit = {
                                Toast.makeText(
                                    context,
                                    "edit",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onCopy = {
                                clipboardHelper.copy(message.text)
                            },
                            onSeen = {
                                if (!message.isRead) {
                                    scope.launch {
                                        chatViewModel.markAsReadMessage(message)
                                    }
                                }
                            })
                    }
                }
            }
            
            when (chatType) {
                ChatType.PRIVATE -> {
                    InputMessage(
                        value = messageText,
                        onValueChange = chatViewModel::changeText,
                        onSendMessage = {
                            scope.launch {
                                val sentMessage = chatViewModel.sendMessage()
                                
                                if (sentMessage != null) {
                                    mainViewModel.onSendMessage(sentMessage)
                                }
                            }
                        })
                }
                
                ChatType.CHANNEL -> {
                    if (profile is ChannelInfo && (profile as ChannelInfo).ownerId == chatViewModel.myId) {
                        InputMessage(
                            value = messageText,
                            onValueChange = chatViewModel::changeText,
                            onSendMessage = {
                                scope.launch {
                                    val sentMessage = chatViewModel.sendMessage()
                                    
                                    if (sentMessage != null) {
                                        mainViewModel.onSendMessage(sentMessage)
                                    }
                                }
                            })
                    } else if (profile is ChannelInfo) {
                        val channelViewModel = hiltViewModel<ChannelViewModel>()
                        
                        var isJoined by remember { mutableStateOf((profile as ChannelInfo).isSubscribed) }
                        
                        if (isJoined) {
                            var isMuted by remember { mutableStateOf(false) }
                            
                            TextButton(
                                shape = RectangleShape,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    isMuted = !isMuted
                                }) {
                                AnimatedContent(
                                    targetState = isMuted,
                                    transitionSpec = {
                                        slideInVertically(tween(200)) { height -> height } + fadeIn(
                                            tween(200)
                                        ) + scaleIn(
                                            tween(200)
                                        ) togetherWith slideOutVertically(tween(200)) { height -> -height } + fadeOut(
                                            tween(200)
                                        ) + scaleOut(tween(200))
                                    }) { isMute ->
                                    Text(
                                        text = if (isMute) {
                                            stringResource(R.string.mute).uppercase()
                                        } else {
                                            stringResource(R.string.unmute).uppercase()
                                        },
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .fillMaxWidth(),
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            TextButton(
                                shape = RectangleShape,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    scope.launch {
                                        val isJoin = channelViewModel.tryJoin(chatInfo.id)
                                        
                                        isJoined = isJoin
                                        
                                        if (isJoin) {
                                            mainViewModel.showNewChat(
                                                chatInfo,
                                                messages.lastOrNull()
                                            )
                                        }
                                    }
                                }) {
                                Text(
                                    text = stringResource(R.string.join).uppercase(),
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                
                ChatType.GROUP -> {}
                ChatType.UNKNOWN -> {}
            }
        }
        
        if (deleteChatDialog.isVisible) {
            DeleteChatDialog(
                onDismissRequest = deleteChatDialog::hide,
                onConfirm = { deleteForReceiver ->
                    scope.launch {
                        val isDeleted = chatViewModel.tryDeleteChat(deleteForReceiver)
                        
                        if (isDeleted) {
                            deleteChatDialog.hide()
                        }
                    }
                },
                chatInfo = chatInfo
            )
        }
        
        if (clearHistoryChatDialog.isVisible) {
            DeleteChatDialog(
                onDismissRequest = clearHistoryChatDialog::hide,
                onConfirm = { deleteForReceiver ->
                    scope.launch {
                        val isDeleted = chatViewModel.tryDeleteChatMessages(deleteForReceiver)
                        
                        if (isDeleted) {
                            clearHistoryChatDialog.hide()
                        }
                    }
                },
                chatInfo = chatInfo
            )
        }
        
        if (deleteMessageDialog.isVisible) {
            DeleteMessageDialog(
                onDismissRequest = deleteMessageDialog::hide,
                onConfirm = { deleteForAll ->
                    scope.launch {
                        selectedMessages.forEach { message ->
                            val isDeleted = chatViewModel.tryDeleteMessage(
                                message.id,
                                deleteForAll
                            )
                            
                            if (isDeleted) {
                                chatViewModel.unselectMessage(message)
                                deleteMessageDialog.hide()
                            }
                        }
                    }
                },
                chatInfo = chatInfo
            )
        }
        
        val channelViewModel = hiltViewModel<ChannelViewModel>()
        
        if (isVisibleLeaveDialog) {
            LeaveChannelDialog(
                onConfirm = {
                    scope.launch {
                        val isLeaved = channelViewModel.tryLeave(chatInfo.id)
                        
                        isVisibleLeaveDialog = false
                        
                        if (isLeaved) {
                            mainViewModel.deleteChat(chatInfo.id)
                            navViewModel.goToMain()
                        }
                    }
                },
                onDismiss = {
                    isVisibleLeaveDialog = false
                },
                channelName = chatInfo.chatName
            )
        }
    }
}

@Composable
private fun EmptyChatPlaceholder() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Напишите сообщение или отправьте стикер",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    chat: ChatInfo,
    subTitle: String,
    dropdownActions: Array<TopBarAction>
) {
    val navViewModel = viewModel<NavigationViewModel>()
    
    val me by UserManager.user.collectAsState()
    
    val isConnected by WebSocketManager.isConnectedState.collectAsState()
    
    val interactionSource = remember { MutableInteractionSource() }
    
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "card_scale_animation"
    )
    
    PageTopBar(
        title = {
            Card(
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            navViewModel.addScreenInStack {
                                ProfileScreen(chat.id)
                            }
                        }),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null
                    )
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        val chatName = if (chat.id == me.id) {
                            stringResource(R.string.saved_messages)
                        } else {
                            chat.chatName
                        }
                        
                        Text(
                            text = chatName,
                            maxLines = 1,
                            fontSize = 18.sp,
                            lineHeight = 16.sp,
                            overflow = TextOverflow.Ellipsis,
                        )
                        
                        AnimatedContent(
                            targetState = isConnected,
                            transitionSpec = { slideInVertically(tween(200)) togetherWith slideOutVertically(tween(200)) }) { isConnected ->
                            if (!isConnected) {
                                Text(
                                    text = "${stringResource(R.string.connecting)}...",
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(
                                    text = subTitle,
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        },
        navigationIcon = NavigationIcon(
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            onClick = navViewModel::removeLastScreenInStack
        ),
        actions = dropdownActions
    )
}

@Composable
private fun DeleteChatDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Boolean) -> Unit,
    chatInfo: ChatInfo
) {
    var deleteForReceiver by remember { mutableStateOf(false) }
    
    CustomDialog(
        title = stringResource(R.string.delete_chat),
        onDismissRequest = onDismissRequest,
        content = {
            val me by UserManager.user.collectAsState()
            
            val chatName = if (chatInfo.id != me.id) " c " + chatInfo.chatName.trimEnd() else ""
            
            Text(
                text = "Удалить чат$chatName без возможности восстановления?",
                lineHeight = 16.sp
            )
            
            if (chatInfo.id != me.id) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            deleteForReceiver = !deleteForReceiver
                        }) {
                    Row(modifier = Modifier.padding(10.dp)) {
                        Checkbox(
                            modifier = Modifier.padding(end = 10.dp),
                            checked = deleteForReceiver,
                            onCheckedChange = null
                        )
                        Text(
                            text = "Также удалить для ${chatInfo.chatName}",
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            } else {
                deleteForReceiver = true
            }
        },
        buttons = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
            
            TextButton(
                onClick = {
                    onConfirm(deleteForReceiver)
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.delete_chat))
            }
        })
}

@Composable
private fun DeleteMessageDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Boolean) -> Unit,
    chatInfo: ChatInfo
) {
    var deleteForReceiver by remember { mutableStateOf(false) }
    
    CustomDialog(
        title = stringResource(R.string.delete_message),
        onDismissRequest = onDismissRequest,
        content = {
            val me by UserManager.user.collectAsState()
            
            Text(
                text = stringResource(R.string.delete_message_description),
                lineHeight = 16.sp
            )
            
            if (chatInfo.id != me.id) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            deleteForReceiver = !deleteForReceiver
                        }) {
                    Row(modifier = Modifier.padding(10.dp)) {
                        Checkbox(
                            modifier = Modifier.padding(end = 10.dp),
                            checked = deleteForReceiver,
                            onCheckedChange = null
                        )
                        Text(
                            text = "${stringResource(R.string.also_delete_for)} ${chatInfo.chatName}",
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            } else {
                deleteForReceiver = true
            }
        },
        buttons = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
            
            TextButton(
                onClick = {
                    onConfirm(deleteForReceiver)
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.delete))
            }
        })
}

@Composable
private fun LeaveChannelDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    channelName: String
) {
    CustomDialog(
        title = stringResource(R.string.leave_channel),
        onDismissRequest = onDismiss,
        buttons = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.leave_channel))
            }
        }) {
        Text(text = buildAnnotatedString {
            append(stringResource(R.string.leave_channel_confirm))
            
            withStyle(style = SpanStyle(fontWeight = FontWeight.W500)) {
                append(
                    " $channelName"
                )
            }
            
            append("?")
        })
    }
}

@Composable
private fun InputMessage(
    value: String,
    onValueChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    attachFile: () -> Unit = { },
) {
    TextField(
        shape = RectangleShape,
        value = value,
        onValueChange = onValueChange,
        maxLines = 5,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(stringResource(R.string.message))
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        
        trailingIcon = {
            Row {
                IconButton(onClick = onSendMessage) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = null,
                    )
                }
            }
        })
}

@Composable
private fun MessageBubble(
    message: Message,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onCopy: () -> Unit,
    onSeen: (Int) -> Unit
) {
    val user by UserManager.user.collectAsState()
    val currentUserId = remember { user.id }
    
    val formatter = SimpleDateFormat(
        "HH:mm",
        Locale.getDefault()
    )
    val sendMessageTime = formatter.format(message.sendTime)
    
    var expanded by remember { mutableStateOf(false) }
    
    val alignment = if (message.senderId == currentUserId) {
        Alignment.CenterEnd
    } else {
        Alignment.CenterStart
    }
    
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            onSeen(message.id)
        }
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                val position = coordinates.positionInParent()
                
                val isElementVisible =
                    position.y >= 0 && position.y < coordinates.parentLayoutCoordinates!!.size.height
                if (isElementVisible) {
                    isVisible = true
                }
            }) {
        
        MessageText(
            text = message.text,
            time = sendMessageTime,
            isRead = if (currentUserId != message.senderId) null else message.isRead,
            alignment = alignment,
            onClick = { expanded = true })
        
        DropdownMenu(
            shape = Shape.DropdownMenu,
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(
                20.dp,
                8.dp
            ),
            properties = PopupProperties(focusable = true),
        ) {
            DropdownMenuItem(
                leadingIcon = {
                    Row(horizontalArrangement = Arrangement.Center) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                text = { Text(stringResource(R.string.copy)) },
                onClick = {
                    expanded = false
                    onCopy()
                })
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = null
                    )
                },
                text = { Text(stringResource(R.string.delete)) },
                onClick = {
                    expanded = false
                    onDelete()
                })
        }
    }
}

@Composable
private fun MessageText(
    text: String,
    time: String,
    isRead: Boolean?,
    alignment: Alignment,
    onClick: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        val parts = text.split(" ")
        
        for (part in parts) {
            if (part.startsWith("@")) {
                pushStringAnnotation(
                    tag = "user",
                    annotation = part
                )
                
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    append(part)
                }
                
                pop()
            } else {
                append(part)
            }
            
            append(" ")
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 1.dp
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = { },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }),
        contentAlignment = alignment
    ) {
        if (isSingleEmoji(text)) {
            Box(
                modifier = Modifier.widthIn(max = 280.dp),
            ) {
                Text(
                    text = text,
                    fontSize = 64.sp,
                    lineHeight = 72.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier
                        .background(
                            color = Color(0x66646464),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = time,
                        style = TextStyle(
                            textAlign = TextAlign.Center
                        ),
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(
                                vertical = 4.dp,
                                horizontal = 6.dp
                            )
                            .align(Alignment.BottomEnd)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .background(
                        color = if (alignment == Alignment.CenterEnd) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else Color(
                            0x66646464
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .widthIn(max = 280.dp)
            ) {
                Text(
                    text = annotatedString,
                    modifier = Modifier
                        .padding(
                            start = 8.dp,
                            top = 6.dp,
                            end = 40.dp,
                            bottom = 6.dp
                        )
                        .clip(shape = RoundedCornerShape(4.dp)),
                    lineHeight = 18.sp
                )
                
                Row(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = time,
                        style = TextStyle(
                            textAlign = TextAlign.Center
                        ),
                        fontSize = 9.sp,
                        color = Color.White,
                        modifier = Modifier.padding(
                            end = if (isRead == null) 8.dp else 0.dp
                        )
                    )
                    
                    if (isRead != null) {
                        Box(
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Outlined.Check,
                                null,
                                Modifier.size(16.dp)
                            )
                            
                            if (isRead) {
                                Icon(
                                    Icons.Outlined.Check,
                                    null,
                                    Modifier
                                        .padding(end = 6.dp)
                                        .size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun isSingleEmoji(text: String): Boolean {
    val emojiRegex =
        Regex("^[\\p{So}\\p{Cntrl}\\p{InEmoticons}\\p{InMiscellaneousSymbolsAndPictographs}\\p{InSupplementalSymbolsAndPictographs}\\uD83C\\uDFF0-\\uD83D\\uDFFF]+$")
    
    return emojiRegex.matches(text.trim())
}

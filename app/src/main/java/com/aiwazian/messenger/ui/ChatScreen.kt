package com.aiwazian.messenger.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.data.ChatInfo
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.services.ClipboardHelper
import com.aiwazian.messenger.services.DialogController
import com.aiwazian.messenger.services.UserManager
import com.aiwazian.messenger.ui.element.CustomDialog
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.viewModels.ChatViewModel
import com.aiwazian.messenger.viewModels.MainScreenViewModel
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import java.time.format.TextStyle as MonthTextStyle

@Composable
fun ChatScreen(chatId: Int) {
    Content(chatId)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(chatId: Int) {
    val context = LocalContext.current
    
    val mainScreenViewModel = hiltViewModel<MainScreenViewModel>()
    
    val chatViewModel = hiltViewModel<ChatViewModel>()
    
    val chatInfo by chatViewModel.chatInfo.collectAsState()
    
    val selectedMessages by chatViewModel.selectedMessages.collectAsState()
    
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        chatViewModel.openChat(chatId)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            chatViewModel.closeChat()
        }
    }
    
    val deleteChatDialog = chatViewModel.deleteChatDialog
    
    val deleteMessageDialog = chatViewModel.deleteMessageDialog
    
    val messageText by chatViewModel.messageText.collectAsState()
    
    Scaffold(
        topBar = {
            TopBar(
                chatInfo,
                deleteChatDialog
            )
        }) { innerPadding ->
        val messages by chatViewModel.messages.collectAsState()
        
        val listState = rememberLazyListState()
        
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(index = messages.lastIndex)
            }
        }
        
        Column(
            modifier = Modifier.padding(innerPadding),
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
                val clipboardHelper = ClipboardHelper(context)
                
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    itemsIndexed(
                        items = messages,
                        key = { _, message -> message.id }) { index, message ->
                        val currentMessageSendDate = Instant.ofEpochMilli(message.sendTime)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        
                        val monthName = currentMessageSendDate.month.getDisplayName(
                            MonthTextStyle.FULL,
                            Locale.getDefault()
                        )
                        
                        val capitalizedMonthName = monthName.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        }
                        
                        val showDateSeparator = if (index > 0) {
                            val previousMessageSendDate =
                                Instant.ofEpochMilli(messages[index - 1].sendTime)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
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
                                        "${currentMessageSendDate.dayOfMonth} $capitalizedMonthName",
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
                                )
                                    .show()
                            },
                            onCopy = {
                                clipboardHelper.copy(message.text)
                            },
                            onSeen = {
                                if (!message.isRead) {
                                    scope.launch {
                                        chatViewModel.markAsRead(message)
                                    }
                                }
                            })
                    }
                }
            }
            
            InputMessage(
                value = messageText,
                onValueChange = chatViewModel::changeText,
                onSendMessage = {
                    scope.launch {
                        val sentMessage = chatViewModel.sendMessage()
                        
                        if (sentMessage != null) {
                            mainScreenViewModel.onSendMessage(sentMessage)
                        }
                    }
                })
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    chat: ChatInfo,
    dialogController: DialogController
) {
    val navViewModel = viewModel<NavigationViewModel>()
    
    var menuExpanded by remember { mutableStateOf(false) }
    val me by UserManager.user.collectAsState()
    
    PageTopBar(
        title = {
            Card(
                shape = RectangleShape,
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                onClick = {
                    navViewModel.addScreenInStack { ProfileScreen(chat.id) }
                }) {
                Column(
                    modifier = Modifier.fillMaxSize(),
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
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = "Delete chat",
                            )
                        },
                        text = { Text(stringResource(R.string.delete_chat)) },
                        onClick = {
                            menuExpanded = false
                            dialogController.show()
                        })
                }
            }
        })
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
            
            val chatName = if (chatInfo.id != me.id) " c " + chatInfo.chatName.trimEnd()
            else ""
            
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
        leadingIcon = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.EmojiEmotions,
                    contentDescription = null,
                )
            }
        },
        trailingIcon = {
            Row {
                IconButton(onClick = attachFile) {
                    Icon(
                        imageVector = Icons.Outlined.AttachFile,
                        contentDescription = null,
                        modifier = Modifier.rotate(45f)
                    )
                }
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
            isRead = message.isRead,
            alignment = alignment,
            onClick = { expanded = true })
        
        DropdownMenu(
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
                            Icons.Outlined.ContentCopy,
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
    isRead: Boolean,
    alignment: Alignment,
    onClick: () -> Unit
) {
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
                            MaterialTheme.colorScheme.primary
                        } else Color(
                            0x66646464
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .widthIn(max = 280.dp)
            ) {
                val startIndex = text.indexOf("https")
                val endIndex = startIndex + "https".length
                
                Text(
                    text = buildAnnotatedString {
                        if (startIndex in text.indices) {
                            append(
                                text.substring(
                                    0,
                                    startIndex
                                )
                            )
                            
                            withStyle(
                                SpanStyle(
                                    color = Color.DarkGray,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append(
                                    text.substring(
                                        startIndex,
                                        endIndex
                                    )
                                )
                            }
                            
                            append(text.substring(endIndex))
                        } else {
                            append(text)
                        }
                    },
                    modifier = Modifier.padding(
                        start = 8.dp,
                        top = 6.dp,
                        end = 40.dp,
                        bottom = 6.dp
                    ),
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
                        color = Color.White
                    )
                    
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

private fun isSingleEmoji(text: String): Boolean {
    val emojiRegex =
        Regex("^[\\p{So}\\p{Cntrl}\\p{InEmoticons}\\p{InMiscellaneousSymbolsAndPictographs}\\p{InSupplementalSymbolsAndPictographs}\\uD83C\\uDFF0-\\uD83D\\uDFFF]+$")
    
    return emojiRegex.matches(text.trim())
}

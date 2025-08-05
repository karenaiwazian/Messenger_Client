package com.aiwazian.messenger.ui

import android.util.Log
import com.aiwazian.messenger.services.ClipboardHelper
import androidx.activity.compose.rememberLauncherForActivityResult
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.utils.ChatStateManager
import com.aiwazian.messenger.R
import com.aiwazian.messenger.services.UserService
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.DeleteChatRequest
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.ui.element.CustomDialog
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.viewModels.ChatViewModel
import com.aiwazian.messenger.viewModels.ChatsViewModel
import com.aiwazian.messenger.viewModels.DialogViewModel
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.TextStyle as MonthTextStyle
import java.time.ZoneId
import java.util.Locale

private var showDeleteChatDialog by mutableStateOf(false)

@Composable
fun ChatScreen(userId: Int) {
    val userState = remember { mutableStateOf(User()) }
    var isLoaded by remember { mutableStateOf(false) }

    val chatsViewModel: ChatsViewModel = viewModel()

    val scope = rememberCoroutineScope()

    val onSend: (Message) -> Unit = { message ->
        val hasChat = chatsViewModel.hasChat(message.chatId)

        if (hasChat) {
            chatsViewModel.updateLastMessage(message.chatId, message)
            chatsViewModel.moveToUp(message.chatId)
        } else {
            scope.launch {
                chatsViewModel.loadUnarchiveChats()
            }
        }
    }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.api.getUserById(userId)

            if (response.isSuccessful) {
                val getUser = response.body()
                if (getUser != null) {
                    userState.value = getUser
                    isLoaded = true
                }
            }
        } catch (e: Exception) {
            Log.e("ChatScreen", e.message.toString())
        }
    }

    if (isLoaded) {
        Content(userState.value, onSend)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(user: User, onSendMessage: (Message) -> Unit) {
    val context = LocalContext.current

    val me by UserService.user.collectAsState()

    val chatViewModel = remember { ChatViewModel(user.id, me.id) }

    LaunchedEffect(Unit) {
        chatViewModel.loadMessages()
    }

    val deleteDialogViewModel: DialogViewModel = viewModel()

    Scaffold(
        topBar = {
            TopBar(user, deleteDialogViewModel)
        }
    ) { innerPadding ->
        val chatId = user.id
        val messages = chatViewModel.messages

        val listState = rememberLazyListState()

        LaunchedEffect(messages.size) {
            ChatStateManager.openChat(chatId)

            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(index = messages.lastIndex)
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                ChatStateManager.closeChat()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Напишите сообщение или отправьте стикер")
                }
            } else {
                val clipboardHelper = ClipboardHelper(context)

                var previewMessageSendDate by remember {
                    mutableStateOf(
                        Instant.ofEpochMilli(Instant.now().epochSecond)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    )
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    items(messages, key = { it.id }) { message ->
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

                        if (previewMessageSendDate.month < currentMessageSendDate.month || previewMessageSendDate.dayOfMonth < currentMessageSendDate.dayOfMonth) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                ) {
                                    Text(
                                        "${currentMessageSendDate.dayOfMonth} $capitalizedMonthName",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        previewMessageSendDate = currentMessageSendDate

                        MessageItem(
                            message = message,
                            onDelete = {
                                Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show()
                            },
                            onEdit = {
                                Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show()
                            },
                            onCopy = {
                                clipboardHelper.copy(message.text)
                            }
                        )
                    }
                }
            }

            val pickMultipleMedia =
                rememberLauncherForActivityResult(PickMultipleVisualMedia(5)) { uris ->
                    if (uris.isNotEmpty()) {
                        Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
                    } else {
                        Log.d("PhotoPicker", "No media selected")
                    }
                }

            val coroutineScope = rememberCoroutineScope()

            InputMessage(
                value = chatViewModel.messageText,
                onValueChange = {
                    chatViewModel.changeText(it)
                },
                attachFile = {
                    pickMultipleMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageAndVideo))
                },
                onSend = {
                    coroutineScope.launch {
                        val sentMessage = chatViewModel.sendMessage()
                        if (sentMessage != null) {
                            onSendMessage(sentMessage)
                        }
                    }
                }
            )

            DeleteChatDialog(
                onDelete = {
                    coroutineScope.launch {
                        val reqBody = DeleteChatRequest(
                            chatId = chatId, deletedBySender = true, deletedByReceiver = false
                        )

                        try {
                            val deleteChat =
                                RetrofitInstance.api.deleteChat(reqBody)

                            if (deleteChat.isSuccessful) {
                                chatViewModel.deleteAllMessages()
                                deleteDialogViewModel.hideDialog()
                            }
                        } catch (e: Exception) {
                            Log.e("DeleteChat", e.message.toString())
                        }
                    }
                },
                dialogViewModel = deleteDialogViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(user: User, dialogViewModel: DialogViewModel) {
    var menuExpanded by remember { mutableStateOf(false) }
    val navViewModel: NavigationViewModel = viewModel()
    val me by UserService.user.collectAsState()

    PageTopBar(
        title = {
            Card(
                shape = RectangleShape,
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                onClick = {
                    navViewModel.addScreenInStack { ProfileScreen(user.id) }
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center
                ) {

                    val chatName = if (user.id == me.id) {
                        stringResource(R.string.saved_messages)
                    } else {
                        "${user.firstName} ${user.lastName}"
                    }

                    Text(chatName)
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        Icons.Default.MoreVert, contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.DeleteOutline,
                                contentDescription = "Delete chat",
                            )
                        },
                        text = { Text(stringResource(R.string.delete_chat)) },
                        onClick = {
                            menuExpanded = false
                            dialogViewModel.showDialog()
                        })
                }
            }
        }
    )
}

@Composable
private fun DeleteChatDialog(onDelete: () -> Unit = {}, dialogViewModel: DialogViewModel) {
    if (showDeleteChatDialog) {
        CustomDialog(
            title = "Удалить чат",
            onDismiss = {
                dialogViewModel.hideDialog()
            },
            onConfirm = {
                onDelete()
            }
        ) {
            Text(
                "Удалить чат без возможности восстановления?",
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
private fun InputMessage(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit = { },
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
                IconButton(onClick = onSend) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = null,
                    )
                }
            }
        })
}

@Composable
fun MessageItem(
    message: Message,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onCopy: () -> Unit,
) {
    val user by UserService.user.collectAsState()
    val currentUserId = remember { user.id }

    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val sendMessageTime = formatter.format(message.sendTime)

    var expanded by remember { mutableStateOf(false) }

    val alignment = if (message.senderId == currentUserId) {
        Alignment.CenterEnd
    } else {
        Alignment.CenterStart
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextMessage(
            text = message.text,
            time = sendMessageTime,
            alignment = alignment,
            onClick = { expanded = true })

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(20.dp, 8.dp),
            properties = PopupProperties(focusable = true),
        ) {
            DropdownMenuItem(leadingIcon = {
                Row(horizontalArrangement = Arrangement.Center) {
                    Icon(
                        Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }, text = { Text("Копировать") }, onClick = {
                expanded = false
                onCopy()
            })
            DropdownMenuItem(leadingIcon = {
                Icon(
                    Icons.Outlined.DeleteOutline, contentDescription = null
                )
            }, text = { Text("Удалить") }, onClick = {
                expanded = false
                onDelete()
            })
        }
    }
}

@Composable
private fun TextMessage(text: String, time: String, alignment: Alignment, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 1.dp)
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
                    contentAlignment = Alignment.BottomEnd, modifier = Modifier
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
                            .padding(vertical = 4.dp, horizontal = 6.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .background(
                        color = if (alignment == Alignment.CenterEnd) MaterialTheme.colorScheme.primary else Color(
                            0x66646464
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .widthIn(max = 280.dp)
            ) {
                Text(
                    text = text, color = Color.White, modifier = Modifier.padding(
                        start = 8.dp, top = 4.dp, end = 40.dp, bottom = 4.dp
                    )
                )

                Text(
                    text = time,
                    style = TextStyle(
                        textAlign = TextAlign.Center
                    ),
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(end = 8.dp, bottom = 4.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }
    }
}

fun isSingleEmoji(text: String): Boolean {
    val emojiRegex =
        Regex("^[\\p{So}\\p{Cntrl}\\p{InEmoticons}\\p{InMiscellaneousSymbolsAndPictographs}\\p{InSupplementalSymbolsAndPictographs}\\uD83C\\uDFF0-\\uD83D\\uDFFF]+$")

    return emojiRegex.matches(text.trim())
}

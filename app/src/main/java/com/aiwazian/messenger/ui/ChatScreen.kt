package com.aiwazian.messenger.ui

import android.util.Log
import com.aiwazian.messenger.utils.ClipboardHelper
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.aiwazian.messenger.utils.UserManager
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.DeleteChatRequest
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.ui.element.CustomDialog
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.viewModels.ChatViewModel
import com.aiwazian.messenger.viewModels.DialogViewModel
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private var showDeleteChatDialog by mutableStateOf(false)

@Composable
fun ChatScreen(userId: Int) {
    val userState = remember { mutableStateOf(User()) }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        val token = UserManager.token

        try {
            val response = RetrofitInstance.api.getUserById(token = "Bearer $token", id = userId)

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
        Content(userState.value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(user: User) {
    val context = LocalContext.current

    val currentUserId = remember { UserManager.user.id }
    val viewModel = remember { ChatViewModel(user.id, currentUserId) }

    val deleteDialogViewModel: DialogViewModel = viewModel()

    Scaffold(
        topBar = {
            TopBar(user, deleteDialogViewModel)
        },
        
    ) { innerPadding ->
        val chatId = user.id
        val messages = viewModel.messages

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
                val clipboardHelper = ClipboardHelper(context = context)

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    items(messages) { message ->
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

            InputMessage(
                value = viewModel.messageText,
                onValueChange = { viewModel.messageText = it },
                attachFile = {
                    pickMultipleMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageAndVideo))
                },
                onSend = {
                    viewModel.sendMessage()
                    viewModel.messageText = ""
                }
            )

            val coroutineScope = rememberCoroutineScope()

            DeleteChatDialog(
                onDelete = {
                    val token = UserManager.token

                    coroutineScope.launch {
                        val reqBody = DeleteChatRequest(
                            chatId = chatId, deletedBySender = true, deletedByReceiver = false
                        )

                        try {
                            val deleteChat =
                                RetrofitInstance.api.deleteChat("Bearer $token", reqBody)

                            if (deleteChat.isSuccessful) {
                                viewModel.deleteAllMessages()
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

                    val chatName = if (user.id == UserManager.user.id) {
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
    val currentUserId = remember { UserManager.user.id }

    val formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())

    val sendMessageTime = formatter.format(Instant.ofEpochMilli(message.timestamp))

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
                        color = if (alignment == Alignment.CenterEnd) MaterialTheme.colorScheme.primary else Color(0x66646464),
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

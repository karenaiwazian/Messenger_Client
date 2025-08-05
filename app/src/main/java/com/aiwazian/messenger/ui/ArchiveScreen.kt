package com.aiwazian.messenger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.ui.element.BottomModalSheet
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SwipeableChatCard
import com.aiwazian.messenger.services.UserService
import com.aiwazian.messenger.viewModels.ChatsViewModel
import com.aiwazian.messenger.viewModels.DialogViewModel
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun ArchiveScreen() {
    Content()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content() {
    val navViewModel: NavigationViewModel = viewModel()
    val scope = rememberCoroutineScope()

    val chatsViewModel: ChatsViewModel = viewModel()
    val chatList by chatsViewModel.archivedChats.collectAsState()

    val dialogViewModel: DialogViewModel = viewModel()

    Scaffold(
        topBar = {
            PageTopBar(
                title = {
                    Text(text = stringResource(R.string.archive))
                }, navigationIcon = {
                    IconButton(onClick = {
                        navViewModel.removeLastScreenInStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null
                        )
                    }
                }, actions = {
                    var menuExpanded by remember { mutableStateOf(false) }

                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = null
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            DropdownMenuItem(leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = null
                                )
                            }, text = {
                                Text(
                                    text = stringResource(R.string.archive_settings),
                                )
                            }, onClick = {
                                menuExpanded = false
                                navViewModel.addScreenInStack {

                                }
                            })
                            DropdownMenuItem(leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.QuestionMark,
                                    contentDescription = null
                                )
                            }, text = {
                                Text(
                                    text = stringResource(R.string.how_does_it_work)
                                )
                            }, onClick = {
                                menuExpanded = false
                                dialogViewModel.showDialog()
                            })
                        }
                    }
                })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            val user by UserService.user.collectAsState()

            LazyColumn {
                items(chatList, key = { it.id }) { chat ->
                    var chatName = chat.chatName

                    if (chat.id == user.id) {
                        chatName = stringResource(R.string.saved_messages)
                    }

                    SwipeableChatCard(chatName = chatName, onClick = {
                        navViewModel.addScreenInStack {
                            ChatScreen(userId = chat.id)
                        }
                    }, backgroundIcon = Icons.Outlined.Unarchive, onDismiss = {
                        scope.launch {
                            chatsViewModel.unarchiveChat(chat.id)
                        }
                    })
                }
            }
        }

        BottomModal(dialogViewModel)
    }
}

@Composable
private fun BottomModal(viewModel: DialogViewModel) {
    BottomModalSheet(viewModel = viewModel, dragHandle = null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Filled.Archive,
                    modifier = Modifier
                        .padding(14.dp)
                        .size(40.dp),
                    contentDescription = null,
                    tint = Color.White,
                )
            }

            Button(
                onClick = {
                    viewModel.hideDialog()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "ОК", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

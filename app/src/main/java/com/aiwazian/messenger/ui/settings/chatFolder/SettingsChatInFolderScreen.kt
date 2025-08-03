package com.aiwazian.messenger.ui.settings.chatFolder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.ChatInfo
import com.aiwazian.messenger.services.TokenManager
import com.aiwazian.messenger.ui.element.InputField
import com.aiwazian.messenger.ui.element.MinimizeChatCard
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.utils.DataStoreManager
import com.aiwazian.messenger.services.UserService
import com.aiwazian.messenger.viewModels.FolderViewModel
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.flow.first

@Composable
fun SettingsChatInFolderScreen(
    onConfirmSelect: (List<ChatInfo>) -> Unit = { }
) {
    Content(onConfirmSelect)
}

@Composable
private fun Content(onConfirmSelect: (List<ChatInfo>) -> Unit = { }) {
    val navViewModel: NavigationViewModel = viewModel()
    val folderViewModel: FolderViewModel = viewModel()

    val initialSelectedChats by folderViewModel.folderChats.collectAsState()

    var allChats by remember { mutableStateOf<List<ChatInfo>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var currentSelectedChats by remember { mutableStateOf(initialSelectedChats) }

    val filteredChats = allChats.filter { chatInfo ->
        chatInfo.chatName.contains(searchQuery.trim(), ignoreCase = true)
    }

    LaunchedEffect(Unit) {
        try {
            val tokenManager = TokenManager()
            val token = tokenManager.getToken()
            val request = RetrofitInstance.api.getAllChats("Bearer $token")
            if (request.isSuccessful) {
                allChats = request.body() ?: emptyList()
            }
        } catch (e: Exception) {
            // Handle exception
        }
    }

    Scaffold(
        topBar = {
            PageTopBar(
                title = {
                    Column {
                        Text("Чаты в папке")
                        Text(
                            "Количество чатов не ограничено",
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navViewModel.removeLastScreenInStack()
                    }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onConfirmSelect(currentSelectedChats)
                        navViewModel.removeLastScreenInStack()
                    }) {
                        Icon(Icons.Outlined.Check, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SectionContainer {
                InputField(
                    placeholder = stringResource(R.string.search),
                    value = searchQuery,
                    onValueChange = { newValue ->
                        searchQuery = newValue
                    })
            }

            SectionContainer {
                val user by UserService.user.collectAsState()

                LazyColumn {
                    items(filteredChats, { it.id }) { chat ->
                        val chatName = if (chat.id == user.id) {
                            stringResource(R.string.saved_messages)
                        } else {
                            chat.chatName
                        }

                        val selected = currentSelectedChats.any { it.id == chat.id }

                        MinimizeChatCard(
                            chatName = chatName,
                            selected = selected,
                            onClick = {
                                currentSelectedChats = if (selected) {
                                    currentSelectedChats - chat
                                } else {
                                    currentSelectedChats + chat
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
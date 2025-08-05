package com.aiwazian.messenger.ui.settings.chatFolder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.services.UserService
import com.aiwazian.messenger.ui.element.InputField
import com.aiwazian.messenger.ui.element.MinimizeChatCard
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionDescription
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.viewModels.FolderViewModel
import com.aiwazian.messenger.viewModels.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsFolderScreen(folderId: Int = 0) {
    Content(folderId)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(folderId: Int) {
    val navViewModel: NavigationViewModel = viewModel()
    val folderViewModel: FolderViewModel = viewModel()
    
    val folderName by folderViewModel.folderName.collectAsState()
    val folderChats by folderViewModel.folderChats.collectAsState()
    val canSave by folderViewModel.canSave.collectAsState()
    
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        if (folderId != 0) {
            folderViewModel.loadFolder(folderId)
        } else {
            folderViewModel.cleanData()
        }
    }
    
    Scaffold(
        topBar = {
            PageTopBar(
                title = {
                    Text(text = folderName.ifBlank { "Новая папка" })
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navViewModel.removeLastScreenInStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = canSave,
                        enter = fadeIn(tween(100)),
                        exit = fadeOut(tween(100))
                    ) {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    folderViewModel.saveFolder()
                                    navViewModel.removeLastScreenInStack()
                                }
                            },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                stringResource(R.string.save).uppercase(),
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            SectionHeader(title = stringResource(R.string.folder_name))
            
            SectionContainer {
                InputField(
                    placeholder = stringResource(R.string.folder_name),
                    value = folderName,
                    onValueChange = {
                        folderViewModel.changeFolderName(it)
                    }
                )
            }
            
            SectionHeader(title = stringResource(R.string.included_chats))
            
            SectionContainer {
                SectionItem(
                    text = stringResource(R.string.add_chats),
                    icon = Icons.Outlined.AddCircle,
                    textColor = MaterialTheme.colorScheme.primary,
                    iconColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        navViewModel.addScreenInStack {
                            SettingsChatInFolderScreen { selectedChats ->
                                folderViewModel.updateChatsForFolder(selectedChats)
                            }
                        }
                    }
                )
                
                val user by UserService.user.collectAsState()
                
                LazyColumn {
                    items(
                        folderChats,
                        { it.id }) { chat ->
                        val chatName = if (chat.id == user.id) {
                            stringResource(R.string.saved_messages)
                        } else {
                            chat.chatName
                        }
                        
                        MinimizeChatCard(chatName)
                    }
                }
            }
            
            SectionDescription(text = "Выберите чаты, которые нужно показывать в этой папке.")
            
            if (folderId != 0) {
                SectionContainer {
                    SectionItem(
                        text = stringResource(R.string.remove_folder),
                        textColor = MaterialTheme.colorScheme.error,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                        onClick = {
                            folderViewModel.showDialog()
                        }
                    )
                }
            }
            
            val showDialog by folderViewModel.idDialogVisible.collectAsState()
            
            if (showDialog) {
                CustomDialogNew(
                    title = stringResource(R.string.remove_folder),
                    onDismissRequest = {
                        folderViewModel.hideDialog()
                    },
                    content = {
                        Text("Это не затронет чаты, находящиеся внутри.")
                    },
                    buttons = {
                        TextButton(onClick = {
                            folderViewModel.hideDialog()
                        }) {
                            Text(stringResource(R.string.cancel))
                        }
                        TextButton(
                            onClick = {
                                scope.launch {
                                    folderViewModel.hideDialog()
                                    folderViewModel.removeFolder(folderId)
                                    navViewModel.removeLastScreenInStack()
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(stringResource(R.string.delete))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CustomDialogNew(
    title: String,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
    buttons: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column {
                Text(
                    text = title,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 20.sp
                )
                
                Box(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                ) {
                    content()
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 4.dp,
                            end = 8.dp
                        ),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        buttons()
                    }
                }
            }
        }
    }
}
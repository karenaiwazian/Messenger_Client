package com.aiwazian.messenger.ui

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.viewModels.CreateGroupViewModel
import com.aiwazian.messenger.viewModels.DialogViewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.utils.UserManager
import com.aiwazian.messenger.utils.VibrateService
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.ChatInfo
import com.aiwazian.messenger.ui.element.BottomModalSheet
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionCheckBoxItem
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.viewModels.NavigationViewModel

@Composable
fun CreateGroupScreen() {
    Content()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content() {
    val context = LocalContext.current
    val colors = LocalCustomColors.current

    val createGroupViewModel: CreateGroupViewModel = viewModel()

    createGroupViewModel.onError = {
        val vibrateService = VibrateService(context)
        vibrateService.vibrate()
    }

    val scrollState = rememberScrollState()

    val initialTopBarColor = colors.secondary
    val scrolledTopBarColor = colors.topAppBarBackground

    val topBarColor = if (scrollState.value > 0) {
        scrolledTopBarColor
    } else {
        initialTopBarColor
    }

    Scaffold(
        topBar = {
            TopBar(topBarColor)
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.imePadding(),
                onClick = {
                    createGroupViewModel.createGroup()
                },
                containerColor = colors.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(colors.secondary)
        ) {
            SectionContainer {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    value = createGroupViewModel.groupName,
                    onValueChange = { createGroupViewModel.changeGroupName(it) },
                    placeholder = { Text("Название группы") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedPlaceholderColor = colors.textHint,
                        unfocusedPlaceholderColor = colors.textHint,
                        focusedTextColor = colors.text,
                        unfocusedTextColor = colors.text
                    )
                )
            }

            val bottomSheetController = remember { DialogViewModel() }

            SectionHeader(
                title = stringResource(R.string.members),
                actionButton = {
                    IconButton(
                        onClick = {
                            bottomSheetController.showDialog()
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = null,
                            tint = colors.textHint
                        )
                    }
                }
            )

            SectionContainer {

            }

            BottomModalSheet(
                viewModel = bottomSheetController,
                content = {
                    var searchUser by remember { mutableStateOf("") }

                    Column {

                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 16.dp),
                            value = searchUser,
                            onValueChange = {
                                searchUser = it
                            },
                            placeholder = {
                                Text(
                                    stringResource(R.string.search),
                                    color = colors.textHint
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.primary,
                                focusedTextColor = colors.text,
                                unfocusedTextColor = colors.text
                            ),
                        )

                        val userChats = remember { mutableStateListOf<ChatInfo>() }

                        LaunchedEffect(Unit) {

                            val token = UserManager.token

                            try {
                                val response = RetrofitInstance.api.getUnarchivedChats("Bearer $token")

                                if (response.isSuccessful) {
                                    userChats.addAll(response.body() ?: emptyList())
                                }
                            } catch (e: Exception) {
                                Log.e("CreateGroupScreen", e.message.toString())
                            }
                        }

                        LazyColumn {
                            items(userChats) { item ->
                                val user = item.chatName

                                SectionCheckBoxItem(text = user, checked = false, onChecked = { })
                            }
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(backgroundColor: Color) {
    val colors = LocalCustomColors.current
    val navViewModel: NavigationViewModel = viewModel()

    PageTopBar(
        title = { Text(text = stringResource(R.string.create_group)) },
        navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = colors.text
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = colors.text,
            containerColor = backgroundColor
        )
    )
}
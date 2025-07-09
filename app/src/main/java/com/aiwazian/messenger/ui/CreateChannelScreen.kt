package com.aiwazian.messenger.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.VibrateManager
import com.aiwazian.messenger.removeLastScreenFromStack
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionDescription
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.viewModels.CreateChannelViewModel

@Composable
fun CreateChannelScreen() {
    Content()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content() {
    val context = LocalContext.current
    val colors = LocalCustomColors.current

    val createChannelViewModel: CreateChannelViewModel = viewModel()

    createChannelViewModel.onError = {
        val vibrateManager = VibrateManager()
        vibrateManager.vibrate(context)
    }

    Scaffold(
        topBar = {
            PageTopBar(
                title = { Text(text = stringResource(R.string.createChannel)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            removeLastScreenFromStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = colors.text
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.imePadding(),
                onClick = { createChannelViewModel.createChannel() },
                containerColor = colors.primary,
                shape = CircleShape
            ) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White)
            }
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
                .background(colors.secondary)
        ) {
            SectionContainer {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    value = createChannelViewModel.channelName,
                    onValueChange = { createChannelViewModel.changeChannelName(it) },
                    placeholder = { Text("Название канала") },
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

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    value = createChannelViewModel.channelBio,
                    onValueChange = { createChannelViewModel.changeChannelBio(it) },
                    placeholder = { Text("Описание") },
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

            SectionDescription("Можете указать дополнительное описание канала.")

        }
    }
}

package com.aiwazian.messenger.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.data.NavigationIcon
import com.aiwazian.messenger.services.VibrateService
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.utils.VibrationPattern
import com.aiwazian.messenger.viewModels.GroupViewModel
import com.aiwazian.messenger.viewModels.NavigationViewModel

@Composable
fun CreateGroupScreen() {
    Content()
}

@Composable
private fun Content() {
    val context = LocalContext.current
    
    val groupViewModel = viewModel<GroupViewModel>()
    
    groupViewModel.onError = {
        val vibrateService = VibrateService(context)
        vibrateService.vibrate(VibrationPattern.Error)
    }
    
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.imePadding(),
                onClick = {
                    groupViewModel.createGroup()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null
                )
            }
        },
        
        ) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            SectionContainer {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    value = groupViewModel.groupName,
                    onValueChange = { newGroupName ->
                        groupViewModel.changeGroupName(newGroupName)
                    },
                    placeholder = { Text("Название группы") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
            }
            
            SectionHeader(
                title = stringResource(R.string.members),
                actionButton = {
                    IconButton(
                        onClick = {
                        
                        }) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = null,
                        )
                    }
                })
            
            SectionContainer {
            
            }
        }
    }
}

@Composable
private fun TopBar() {
    val navViewModel = viewModel<NavigationViewModel>()
    
    PageTopBar(
        title = { Text(text = stringResource(R.string.create_group)) },
        navigationIcon = NavigationIcon(
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            onClick = navViewModel::removeLastScreenInStack
        )
    )
}
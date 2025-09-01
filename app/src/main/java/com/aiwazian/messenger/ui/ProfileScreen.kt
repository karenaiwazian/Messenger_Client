package com.aiwazian.messenger.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.services.ClipboardHelper
import com.aiwazian.messenger.services.UserManager
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionItem
import com.aiwazian.messenger.ui.element.SectionToggleItem
import com.aiwazian.messenger.ui.settings.SettingsProfileScreen
import com.aiwazian.messenger.viewModels.NavigationViewModel
import com.aiwazian.messenger.viewModels.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ProfileScreen(profileId: Int) {
    Content(profileId)
}

@Composable
private fun Content(profileId: Int) {
    val navViewModel = viewModel<NavigationViewModel>()
    val profile = viewModel<ProfileViewModel>()
    
    LaunchedEffect(Unit) {
        profile.open(profileId)
    }
    
    val user by profile.user.collectAsState()
    
    val context = LocalContext.current
    
    val scrollState = rememberScrollState()
    
    val clipboardHelper = ClipboardHelper(context = context)
    
    Scaffold(
        topBar = { DefaultTopBar(user) },
    ) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            SectionHeader(title = stringResource(R.string.information))
            
            SectionContainer {
                val userBio = user.bio
                
                if (userBio.isNotEmpty()) {
                    SectionItem(
                        text = userBio,
                        description = stringResource(R.string.bio),
                        onLongClick = {
                            clipboardHelper.copy("userBio")
                        })
                }
                
                val username = user.username
                
                if (!username.isNullOrBlank()) {
                    SectionItem(
                        text = ("@$username"),
                        description = stringResource(R.string.username),
                        onLongClick = {
                            clipboardHelper.copy(username)
                        },
                        primaryIcon = Icons.Outlined.QrCode,
                        primaryIconClick = {
                            navViewModel.addScreenInStack { QRCodeScreen() }
                        })
                }
                
                val dateOfBirth = user.dateOfBirth
                
                if (dateOfBirth != null) {
                    val date = SimpleDateFormat(
                        "d MMM yyyy",
                        Locale.getDefault()
                    ).format(dateOfBirth)
                    
                    SectionItem(
                        text = date,
                        description = stringResource(R.string.date_of_birth)
                    )
                }
                
                val me by UserManager.user.collectAsState()
                
                if (user.id != me.id) {
                    SectionToggleItem(text = stringResource(R.string.notifications))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTopBar(user: User) {
    val navViewModel = viewModel<NavigationViewModel>()
    var myId by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(Unit) {
        myId = UserManager.user.value.id
    }
    
    PageTopBar(
        title = {
            Text(
                "${user.firstName} ${user.lastName}",
                maxLines = 1,
                softWrap = false
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        actions = {
            if (user.id == myId && user.id != 0) {
                IconButton(
                    onClick = {
                        navViewModel.addScreenInStack { SettingsProfileScreen() }
                    }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                    )
                }
            }
        })
}
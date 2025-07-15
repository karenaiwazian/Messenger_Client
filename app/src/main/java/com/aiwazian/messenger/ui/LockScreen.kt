package com.aiwazian.messenger.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.ui.element.CodeBlocks
import com.aiwazian.messenger.ui.element.CustomNumberBoard
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.utils.VibrateService
import com.aiwazian.messenger.utils.VibrationPattern
import com.aiwazian.messenger.viewModels.LockScreenViewModel

@Composable
fun LockScreen() {
    Content()
}

@Composable
private fun Content() {
    val colors = LocalCustomColors.current
    val lockScreenViewModel: LockScreenViewModel = viewModel()
    val vibrateService = VibrateService(LocalContext.current)

    lockScreenViewModel.onWrongPasscode = {
        vibrateService.vibrate(VibrationPattern.Error)
        lockScreenViewModel.clearPasscode()
    }

    Scaffold(containerColor = colors.secondary) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(80.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Lock",
                    modifier = Modifier.size(40.dp),
                    tint = colors.text
                )

                CodeBlocks(
                    count = lockScreenViewModel.MAX_LENGTH_PASSCODE,
                    code = lockScreenViewModel.passcode
                )

                CustomNumberBoard(
                    value = lockScreenViewModel.passcode,
                    onChange = lockScreenViewModel::onPasscodeChanged
                )
            }
        }
    }
}

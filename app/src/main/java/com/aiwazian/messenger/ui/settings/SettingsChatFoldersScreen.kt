package com.aiwazian.messenger.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aiwazian.messenger.utils.JsonAnimation
import com.aiwazian.messenger.R
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.viewModels.NavigationViewModel

@Composable
fun SettingsChatFoldersScreen() {
    Content()
}

@Composable
private fun Content() {
    Scaffold(
        topBar = {
            TopBar()
        },
        
    ) { innerPadding ->

        Column(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val composition by rememberLottieComposition(
                    spec = LottieCompositionSpec.Asset(JsonAnimation.FOLDERS)
                )

                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.size(100.dp),
                    iterations = LottieConstants.IterateForever,
                    isPlaying = true
                )

                Text(
                    text = "Вы можете создать папки с нужными чатами и переключаться между ними.",
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            SectionHeader(title = stringResource(R.string.chat_folders))

            SectionContainer {

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    val navViewModel: NavigationViewModel = viewModel()

    PageTopBar(
        title = { Text(stringResource(R.string.chat_folders)) },
        navigationIcon = {
            IconButton(onClick = {
                navViewModel.removeLastScreenInStack()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        }
    )
}

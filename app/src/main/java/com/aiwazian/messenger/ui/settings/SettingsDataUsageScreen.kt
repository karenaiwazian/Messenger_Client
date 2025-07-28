package com.aiwazian.messenger.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.viewModels.DataUsageViewModel
import com.aiwazian.messenger.viewModels.DialogViewModel
import com.aiwazian.messenger.ui.element.CustomDialog
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.viewModels.NavigationViewModel
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun SettingsDataUsageScreen() {
    Content()
}

@Composable
private fun Content(viewModel: DataUsageViewModel = viewModel()) {
    val context = LocalContext.current

    viewModel.reload(context)

    val cacheSize = viewModel.cacheSize
    val cacheMb = cacheSize / (1024.0 * 1024.0)
    val cacheMbRounded = BigDecimal(cacheMb).setScale(2, RoundingMode.HALF_UP).toDouble()

    val sizeBytes = viewModel.appSize
    val sizeMb = sizeBytes / (1024.0 * 1024.0)
    val sizeMbRounded = BigDecimal(sizeMb).setScale(2, RoundingMode.HALF_UP).toDouble()

    val dialogViewModel: DialogViewModel = viewModel()

    Scaffold(
        topBar = { TopBar() },
        
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Использование памяти",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500
                )
                Text(text = "$sizeMbRounded MB")
            }

            SectionContainer {
                Column(Modifier.padding(10.dp)) {

                    Button(
                        onClick = { dialogViewModel.showDialog() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Очистить кеш $cacheMbRounded MB",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 16.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        ClearCacheDialog(dialogViewModel = dialogViewModel, onPrimary = {
            val cacheDir = context.cacheDir
            cacheDir.deleteRecursively()
            viewModel.reload(context)
            dialogViewModel.hideDialog()
        })
    }
}

@Composable
private fun ClearCacheDialog(
    dialogViewModel: DialogViewModel,
    onPrimary: () -> Unit,
) {
    if (dialogViewModel.isDialogVisible.value) {
        CustomDialog(
            title = "Очистить кеш",
            onConfirm = {
                onPrimary()
            },
            onDismiss = { dialogViewModel.hideDialog() }
        ) {
            Text(
                text = "Все медиа останутся в облаке, при необходимости Вы сможете заново загрузить их снова.",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    val navViewModel: NavigationViewModel = viewModel()

    PageTopBar(
        title = { },
        navigationIcon = {
            IconButton(
                onClick = {
                    navViewModel.removeLastScreenInStack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    )
}
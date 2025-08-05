package com.aiwazian.messenger.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aiwazian.messenger.viewModels.AuthViewModel
import com.aiwazian.messenger.viewModels.DialogViewModel
import com.aiwazian.messenger.R
import com.aiwazian.messenger.ui.element.CustomDialog

@Composable
fun LoginScreen(viewModel: AuthViewModel, navController: NavHostController) {
    var isLoad by remember { mutableStateOf(true) }

    val dialogViewModel: DialogViewModel = viewModel()

    var modalText by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isLoad = false
                    viewModel.findUserByLogin(
                        find = {
                            modalText = "Пользователь найден. Продолжить?"
                            dialogViewModel.primaryAction = {
                                navController.navigate(Screen.PASSWORD)
                            }
                            dialogViewModel.showDialog()
                            isLoad = true
                        },
                        notFind = {
                            modalText = "Пользователь не найден. Создать?"
                            dialogViewModel.primaryAction = {
                                navController.navigate(Screen.PASSWORD)
                            }
                            dialogViewModel.showDialog()
                            isLoad = true
                        },
                        error = {
                            modalText = "Не удалось проверить, поробуйте ещё раз."
                            dialogViewModel.primaryAction = {
                                dialogViewModel.hideDialog()
                            }
                            dialogViewModel.showDialog()
                            isLoad = true
                        }
                    )
                },
                modifier = Modifier.imePadding(),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                if (isLoad) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Логин пользователя",
                modifier = Modifier.padding(vertical = 40.dp),
                fontSize = 28.sp
            )
            Column(Modifier.width(300.dp)) {
                LoginField(
                    viewModel.login,
                    viewModel::onLoginChanged,
                )
            }
        }

        if (dialogViewModel.isDialogVisible.value) {
            CustomDialog(
                title = stringResource(R.string.app_name),
                onDismiss = {
                    dialogViewModel.hideDialog()
                    dialogViewModel.dismissAction?.invoke()
                },
                onConfirm = {
                    dialogViewModel.hideDialog()
                    dialogViewModel.primaryAction?.invoke()
                },
                dismissButtonText = "Отмена",
                primaryButtonText = "Да"
            ) {
                Text(
                    text = modalText,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Логин",
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            errorLabelColor = MaterialTheme.colorScheme.error,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorTextColor = MaterialTheme.colorScheme.error
        ),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
    )
}

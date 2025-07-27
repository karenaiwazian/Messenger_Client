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
import com.aiwazian.messenger.ui.theme.LocalCustomColors

@Composable
fun LoginScreen(viewModel: AuthViewModel, navController: NavHostController) {
    val colors = LocalCustomColors.current

    var isLoad by remember { mutableStateOf(true) }

    val dialogViewModel: DialogViewModel = viewModel()

    var modalText by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = colors.secondary,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isLoad = false
                    viewModel.findUserByLogin(
                        find = {
                            modalText = "Пользователь найден. Продолжить?"
                            dialogViewModel.primaryAction = {
                                navController.navigate(Screen.Password)
                            }
                            dialogViewModel.showDialog()
                            isLoad = true
                        },
                        notFind = {
                            modalText = "Пользователь не найден. Создать?"
                            dialogViewModel.primaryAction = {
                                navController.navigate(Screen.Password)
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
                containerColor = colors.primary,
                shape = CircleShape
            ) {
                if (isLoad) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                } else {
                    CircularProgressIndicator(
                        color = Color.White,
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
                color = colors.text,
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
                    color = colors.text,
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
    val colors = LocalCustomColors.current

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.primary,
            focusedTextColor = colors.text,
            unfocusedTextColor = colors.text,
            focusedPlaceholderColor = colors.textHint,
            unfocusedPlaceholderColor = colors.textHint,
            focusedLabelColor = colors.primary,
            cursorColor = colors.primary,
            errorLabelColor = colors.danger,
            errorBorderColor = colors.danger,
            errorTextColor = colors.danger
        ),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
    )
}

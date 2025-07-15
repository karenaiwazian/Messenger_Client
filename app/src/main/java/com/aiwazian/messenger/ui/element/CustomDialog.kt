package com.aiwazian.messenger.ui.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aiwazian.messenger.ui.theme.LocalCustomColors

@Composable
fun CustomDialog(
    title: String,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    dismissButtonText: String = "Отмена",
    primaryButtonText: String = "Ок",
    content: @Composable () -> Unit = {},
) {
    val colors = LocalCustomColors.current

    Dialog(onDismissRequest = onDismiss) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.background)
                ) {
                    Text(
                        text = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colors.text
                    )

                    content()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(
                            shape = RoundedCornerShape(8.dp),
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = colors.primary
                            )
                        ) {
                            Text(dismissButtonText, color = colors.primary)
                        }
                        
                        Spacer(Modifier.width(10.dp))

                        TextButton(
                            shape = RoundedCornerShape(8.dp),
                            onClick = onConfirm,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = colors.primary
                            )
                        ) {
                            Text(primaryButtonText, color = colors.primary)
                        }
                    }
                }
            }
        }
    }
}
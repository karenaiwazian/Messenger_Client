package com.aiwazian.messenger.ui.element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.aiwazian.messenger.utils.VibrateService
import com.aiwazian.messenger.utils.VibrationPattern

@Composable
fun CustomNumberBoard(
    value: String = "",
    onChange: (String) -> Unit = { }
) {
    val colors = LocalCustomColors.current

    val context = LocalContext.current

    val vibrateService = VibrateService(context)

    Column(
        modifier = Modifier
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf(null, "0", Icons.AutoMirrored.Outlined.Backspace),
        )

        keys.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { key ->
                    if (key == null) {
                        Box(modifier = Modifier.weight(1f))
                        return@forEach
                    }

                    NumberButton(
                        onClick = {
                            if (key is ImageVector) {
                                onChange(value.dropLast(1))
                            } else {
                                onChange(value + key)
                            }

                            vibrateService.vibrate(
                                pattern = VibrationPattern.TactileResponse
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 2.dp)
                    ) {
                        if (key is ImageVector) {
                            Text(text = "", lineHeight = 30.sp)

                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                                contentDescription = null,
                                tint = colors.text
                            )
                        } else if (key is String) {
                            Text(
                                text = key,
                                color = colors.text,
                                fontSize = 18.sp,
                                lineHeight = 30.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = LocalCustomColors.current

    val clickedColor = colors.textHint

    TextButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = clickedColor,
            containerColor = colors.background
        )
    ) {
        content()
    }
}

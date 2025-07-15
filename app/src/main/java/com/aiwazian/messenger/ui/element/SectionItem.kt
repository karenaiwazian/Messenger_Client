package com.aiwazian.messenger.ui.element

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiwazian.messenger.ui.theme.LocalCustomColors

@Composable
fun SectionItem(
    text: String,
    icon: ImageVector? = null,
    description: String? = null,
    primaryText: String? = null,
    primaryIcon: ImageVector? = null,
    primaryIconClick: () -> Unit = {},
    textColor: Color? = null,
    iconColor: Color? = null,
    colors: ButtonColors? = null,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    val customColors = LocalCustomColors.current

    TextButton(
        shape = RectangleShape,
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = colors ?: ButtonDefaults.textButtonColors(contentColor = customColors.textHint),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor ?: customColors.textHint,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    color = textColor ?: customColors.text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )

                if (description != null) {
                    Text(
                        text = description,
                        color = customColors.textHint,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            if (primaryText != null) {
                Text(
                    text = primaryText,
                    color = customColors.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            } else if (primaryIcon != null) {
                IconButton(onClick = primaryIconClick) {
                    Icon(
                        imageVector = primaryIcon,
                        contentDescription = null,
                        tint = customColors.primary
                    )
                }
            }
        }
    }
}

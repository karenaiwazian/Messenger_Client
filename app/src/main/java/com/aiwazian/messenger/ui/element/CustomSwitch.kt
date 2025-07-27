package com.aiwazian.messenger.ui.element

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aiwazian.messenger.ui.theme.LocalCustomColors

@Composable
fun CustomSwitch(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?) {
    val colors = LocalCustomColors.current

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        thumbContent = {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (checked) colors.background else colors.textHint),
                contentAlignment = Alignment.Center
            ) { }
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.Transparent,
            uncheckedThumbColor = Color.Transparent,
            checkedTrackColor = colors.primary,
            uncheckedTrackColor = colors.background
        )
    )
}
package com.aiwazian.messenger.ui.element

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiwazian.messenger.R
import com.aiwazian.messenger.ui.theme.LocalCustomColors

@Composable
fun SectionDescription(text: String) {
    val customColors = LocalCustomColors.current

    Text(
        text = text,
        color = customColors.textHint,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    )
}

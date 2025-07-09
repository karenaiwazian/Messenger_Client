package com.aiwazian.messenger.ui.element

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiwazian.messenger.R
import com.aiwazian.messenger.ui.theme.LocalCustomColors

@Composable
fun SectionRadioItem(text: String, selected: Boolean, onClick: () -> Unit = {}) {
    val customColors = LocalCustomColors.current

    TextButton(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = customColors.textHint
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                onClick = null,
                selected = selected,
                modifier = Modifier.padding(end = 16.dp),
                colors = RadioButtonDefaults.colors(
                    selectedColor = customColors.primary
                ),
            )

            Text(
                text = text,
                color = customColors.text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
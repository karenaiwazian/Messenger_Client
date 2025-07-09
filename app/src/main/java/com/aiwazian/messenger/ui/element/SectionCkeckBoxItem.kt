package com.aiwazian.messenger.ui.element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiwazian.messenger.ui.theme.LocalCustomColors

@Composable
fun SectionCheckBoxItem(text: String, checked: Boolean, onChecked: () -> Unit = {}) {
    val customColors = LocalCustomColors.current
    var isChecked by remember { mutableStateOf(checked) }
    val colors = LocalCustomColors.current

    Column {
        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.textButtonColors(
                contentColor = colors.text
            ),
            onClick = {
                isChecked = !isChecked
                onChecked()
            }
        ) {
            Row(
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Checkbox(
                    onCheckedChange = null,
                    checked = isChecked,
                    colors = CheckboxDefaults.colors(
                        checkedColor = colors.primary
                    )
                )
                Text(text = text, color = customColors.text, fontWeight = FontWeight.W400)
            }
        }
    }
}
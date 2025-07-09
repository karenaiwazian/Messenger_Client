package com.aiwazian.messenger.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aiwazian.messenger.removeLastScreenFromStack
import com.aiwazian.messenger.ui.element.PageTopBar
import com.aiwazian.messenger.ui.element.SectionContainer
import com.aiwazian.messenger.ui.element.SectionDescription
import com.aiwazian.messenger.ui.element.SectionHeader
import com.aiwazian.messenger.ui.element.SectionRadioItem
import com.aiwazian.messenger.ui.theme.LocalCustomColors

@Composable
fun TimeOfEntryScreen() {
    Content()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content() {
    val colors = LocalCustomColors.current
    Scaffold(
        topBar = {
            PageTopBar(
                title = { Text("Время захода") },
                navigationIcon = {
                    IconButton(onClick = { removeLastScreenFromStack() }) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = colors.text
                        )
                    }
                })
        },
        containerColor = colors.secondary
    ) {
        Column(Modifier.padding(it)) {
            SectionHeader("Кто видит время моего последнего захода?")

            SectionContainer {
                SectionRadioItem("Все", selected = true)
                SectionRadioItem("Никто", selected = false)
            }

            SectionDescription("Вместо точного времени будет видно примерное значение (недавно, на этой неделе, в этом месяце)")
        }
    }
}

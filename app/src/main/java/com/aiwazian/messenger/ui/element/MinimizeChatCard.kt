package com.aiwazian.messenger.ui.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MinimizeChatCard(chatName: String, selected: Boolean = false, onClick: () -> Unit = { }) {
    ListItem(
        modifier = Modifier.clickable {
            onClick()
        }, leadingContent = {
            Box(modifier = Modifier.size(40.dp)) {
                Icon(Icons.Outlined.AccountCircle, null, modifier = Modifier.fillMaxSize())

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    AnimatedVisibility(
                        modifier = Modifier.background(Color.Green),
                        visible = selected,
                        enter = fadeIn(animationSpec = tween(durationMillis = 100)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 100))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }, headlineContent = {
            Text(chatName)
        }, colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

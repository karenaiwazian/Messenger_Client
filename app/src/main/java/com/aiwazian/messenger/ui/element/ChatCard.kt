package com.aiwazian.messenger.ui.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiwazian.messenger.data.Message

@Composable
fun ChatCard(
    chatName: String,
    lastMessage: Message? = null,
    selected: Boolean = false,
    pinned: Boolean = false,
    unreadMessageCount: Int = 0,
    onClickChat: () -> Unit = {},
    onLongClickChat: () -> Unit = {},
    onLongClickChatLogo: () -> Unit = {}
) {
    ListItem(
        modifier = Modifier.combinedClickable(
            onClick = {
                onClickChat()
            },
            onLongClick = {
                onLongClickChat()
            }),
        headlineContent = {
            Text(chatName)
        },
        supportingContent = {
            if (lastMessage != null) {
                Text(lastMessage.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        },
        leadingContent = {
            Leading(selected)
        },
        trailingContent = {
            if (unreadMessageCount > 0) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White
                ) {
                    Text(
                        text = unreadMessageCount.toString(),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            } else if (pinned) {
                Badge(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PushPin,
                        contentDescription = null,
                    )
                }
            }
        }
    )
}

@Composable
private fun Leading(visible: Boolean) {
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
                visible = visible,
                enter = fadeIn(tween(100)),
                exit = fadeOut(tween(100))
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
}
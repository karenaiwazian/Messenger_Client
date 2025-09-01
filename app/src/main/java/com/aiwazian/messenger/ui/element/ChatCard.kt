package com.aiwazian.messenger.ui.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiwazian.messenger.data.Message
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.days

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
            Text(
                text = chatName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            if (lastMessage != null) {
                Text(
                    text = lastMessage.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        leadingContent = {
            Leading(selected)
        },
        trailingContent = {
            Column {
                if (lastMessage != null) {
                    LastMessageSendTime(lastMessage.sendTime)
                }
                
                Box(modifier = Modifier.size(40.dp)) {
                    if (unreadMessageCount > 0) {
                        UnreadMessageCount(unreadMessageCount)
                    } else if (pinned) {
                        PinIcon()
                    }
                }
            }
        })
}

@Composable
private fun LastMessageSendTime(sendTime: Long) {
    val sendMessageTime = formatTimestamp(sendTime)
    
    Text(sendMessageTime)
}

@Composable
private fun PinIcon() {
    Badge(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Icon(
            imageVector = Icons.Outlined.PushPin,
            contentDescription = null,
            modifier = Modifier.rotate(45f),
        )
    }
}

@Composable
private fun UnreadMessageCount(count: Int) {
    Badge(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {
        Text(
            text = count.toString(),
            fontSize = 14.sp,
            modifier = Modifier.padding(2.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun Leading(visible: Boolean) {
    Box(modifier = Modifier.size(40.dp)) {
        Icon(
            Icons.Outlined.AccountCircle,
            null,
            modifier = Modifier.fillMaxSize()
        )
        
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

private fun formatTimestamp(timestamp: Long): String {
    val currentDateTime = LocalDateTime.now()
    val providedDateTime = Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
    
    if (providedDateTime.toLocalDate().isEqual(currentDateTime.toLocalDate())) {
        return providedDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    
    val startOfWeek = currentDateTime.toLocalDate().minusDays(currentDateTime.dayOfWeek.ordinal.toLong())
    if (providedDateTime.toLocalDate().isAfter(startOfWeek) && providedDateTime.toLocalDate().isBefore(currentDateTime.toLocalDate())) {
        val dayOfWeekFormatter = DateTimeFormatter.ofPattern("E")
        return providedDateTime.format(dayOfWeekFormatter)
    }
    
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM")
    return providedDateTime.format(dateFormatter)
}

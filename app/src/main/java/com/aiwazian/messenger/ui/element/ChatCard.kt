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
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiwazian.messenger.ui.theme.LocalCustomColors

@Composable
fun ChatCard(
    chatName: String,
    lastMessage: String,
    selected: Boolean = false,
    pinned: Boolean = false,
    unreadMessageCount: Int = 0,
    onClickChat: () -> Unit = {},
    onLongClickChat: () -> Unit = {},
    onLongClickChatLogo: () -> Unit = {}
) {
    val colors = LocalCustomColors.current

    ListItem(
        modifier = Modifier.combinedClickable(onClick = {
            onClickChat()
        }, onLongClick = {
            onLongClickChat()
        }), headlineContent = {
            Text(chatName)
        }, supportingContent = {
            Text(lastMessage)
        }, leadingContent = {
            Box(modifier = Modifier.size(40.dp)) {
                Icon(Icons.Outlined.AccountCircle, null)

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
        }, trailingContent = {
            if (unreadMessageCount > 0) {
                Badge(
                    containerColor = colors.primary, contentColor = Color.White
                ) {
                    Text(
                        text = unreadMessageCount.toString(),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            } else if (pinned) {
                Badge(
                    containerColor = Color.Transparent, contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PushPin,
                        contentDescription = null,
                        tint = colors.text
                    )
                }
            }
        }, colors = ListItemDefaults.colors(
            containerColor = colors.background,
            headlineColor = colors.text,
            leadingIconColor = colors.text,
            supportingColor = colors.textHint
        )
    )
}

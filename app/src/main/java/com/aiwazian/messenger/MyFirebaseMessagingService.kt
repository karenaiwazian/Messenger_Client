package com.aiwazian.messenger

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationCompat
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.FcmTokenRequest
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")

        sendTokenToServer(token)
    }

    companion object {
        fun sendTokenToServer(token: String) {
            val userId = UserManager.token

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val api = RetrofitInstance.api
                    val f = FcmTokenRequest(token = token)
                    val response = api.updateFcmToken("Bearer $userId", f)
                    if (response.isSuccessful) {
                        Log.d("FCM", "FCM token updated on server")
                    } else {
                        Log.e("FCM", "Failed to update token: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("FCM", "Error updating token: ${e.message}")
                }
            }
        }
    }

    val messagesMap = mutableMapOf<String, MutableList<String>>() // senderId -> messages

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val chatId = remoteMessage.data["chatId"] ?: return
        val title = remoteMessage.data["title"] ?: "Messenger"
        val body = remoteMessage.data["body"] ?: ""

        val list = messagesMap.getOrPut(chatId) { mutableListOf() }
        list.add(body)

        if (!ChatStateManager.isChatOpen(chatId)) {
            showNotification(chatId = chatId, title = title, message = body, messages = list)
        }
    }

    private fun showNotification(chatId: String, title: String, message: String, messages: List<String>) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "chat_channel"

        val vibrationPattern1 = longArrayOf(0, 300, 200, 300)

        val channel = NotificationChannel(
            channelId,
            "Личные сообщения",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            vibrationPattern = vibrationPattern1
        }

        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra("chatId", chatId)

        val requestCode = System.currentTimeMillis().toInt()

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        val groupKey = chatId

        val inboxStyle = NotificationCompat.InboxStyle()
        messages.forEach { inboxStyle.addLine(it) }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.new_app_icon_round)
            .setAutoCancel(true)
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setStyle(inboxStyle)
            .setContentIntent(pendingIntent)
            .setVibrate(vibrationPattern1)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }
}

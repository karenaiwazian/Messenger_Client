package com.aiwazian.messenger.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aiwazian.messenger.MainActivity
import com.aiwazian.messenger.data.Notification
import com.aiwazian.messenger.interfaces.NotificationService
import com.aiwazian.messenger.R
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.NotificationTokenRequest
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class NotificationService : FirebaseMessagingService(), NotificationService {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        sendTokenToServer(token)
    }

    fun sendTokenToServer(token: String? = null) {

        if (token.isNullOrBlank()) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result

                    val notificationService = NotificationService()

                    notificationService.sendTokenToServer(token)

                    Log.e("FCM", "success $token")
                } else {
                    Log.e("FCM", "Error ${task.exception}")
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userId = UserManager.token
                    val api = RetrofitInstance.api
                    val request = NotificationTokenRequest(token = token)
                    val response = api.updateFcmToken("Bearer $userId", request)

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

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val chatId = remoteMessage.data["chatId"]?.toIntOrNull() ?: return
        val title = remoteMessage.data["title"] ?: "Messenger"
        val body = remoteMessage.data["body"] ?: ""

        if (!ChatStateManager.isChatOpen(chatId)) {
            val notification = Notification(chatId, title, body)
            showNotification(notification = notification)
        }
    }

    override fun showNotification(notification: Notification) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "chat_channel"

        val vibrationPattern = VibrationPattern.Notification

        val channel = NotificationChannel(
            channelId,
            "Личные сообщения",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            this.vibrationPattern = vibrationPattern
        }

        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra("chatId", notification.chatId)

        val requestCode = System.currentTimeMillis().toInt()

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        val groupKey = notification.chatId.toString()

        val inboxStyle = NotificationCompat.InboxStyle()

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setSmallIcon(R.mipmap.new_app_icon_round)
            .setAutoCancel(true)
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setStyle(inboxStyle)
            .setContentIntent(pendingIntent)
            .setVibrate(vibrationPattern)
            .build()

        notificationManager.notify(Random.Default.nextInt(), notification)
    }
}
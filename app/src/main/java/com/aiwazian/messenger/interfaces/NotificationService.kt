package com.aiwazian.messenger.interfaces

import com.aiwazian.messenger.data.Notification

interface NotificationService {
    fun showNotification(notification: Notification)
}
package com.example.paxrioverde.util

interface NotificationScheduler {
    fun scheduleNotification(
        id: Int,
        title: String,
        message: String,
        epochSeconds: Long
    )
    fun cancelAllNotifications()
    fun requestPermission()
}

expect fun getNotificationScheduler(): NotificationScheduler

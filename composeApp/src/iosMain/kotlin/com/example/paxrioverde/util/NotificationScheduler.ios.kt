package com.example.paxrioverde.util

import platform.UserNotifications.*
import platform.Foundation.*

class IosNotificationScheduler : NotificationScheduler {
    override fun scheduleNotification(id: Int, title: String, message: String, epochSeconds: Long) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
            setSound(UNNotificationSound.defaultSound())
            setUserInfo(mapOf("navigate_to" to "finance"))
        }

        val date = NSDate.dateWithTimeIntervalSince1970(epochSeconds.toDouble())
        val calendar = NSCalendar.currentCalendar
        val components = calendar.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or NSCalendarUnitHour or NSCalendarUnitMinute,
            fromDate = date
        )

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(components, repeats = false)
        val request = UNNotificationRequest.requestWithIdentifier(id.toString(), content, trigger)

        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { error ->
            if (error != null) {
                println("Error scheduling notification: ${error.localizedDescription}")
            }
        }
    }

    override fun cancelAllNotifications() {
        UNUserNotificationCenter.currentNotificationCenter().removeAllPendingNotificationRequests()
    }

    override fun requestPermission() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge) { granted, error ->
            if (error != null) {
                println("Error requesting notification authorization: ${error.localizedDescription}")
            }
        }
    }
}

actual fun getNotificationScheduler(): NotificationScheduler {
    return IosNotificationScheduler()
}

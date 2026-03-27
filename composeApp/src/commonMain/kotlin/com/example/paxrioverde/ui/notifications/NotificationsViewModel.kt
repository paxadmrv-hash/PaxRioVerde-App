package com.example.paxrioverde.ui.notifications

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object NotificationCenter {
    private val _notifications = mutableStateListOf<NotificationItem>()
    val notifications: List<NotificationItem> = _notifications

    fun addNotification(title: String, message: String, type: NotificationType) {
        val id = (_notifications.maxOfOrNull { it.id } ?: 0) + 1
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val timeStr = "${now.dayOfMonth.toString().padStart(2, '0')}/${now.monthNumber.toString().padStart(2, '0')} ${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
        
        _notifications.add(0, NotificationItem(id, title, message, timeStr, type))
    }

    fun clear() {
        _notifications.clear()
    }

    init {
        if (_notifications.isEmpty()) {
            _notifications.add(NotificationItem(1, "Bem-vindo!", "Obrigado por instalar o novo App Grupo Universo.", "01/11 08:00", NotificationType.SYSTEM, true))
        }
    }
}

class NotificationsViewModel : ViewModel() {
    val notifications: List<NotificationItem> = NotificationCenter.notifications

    fun limparNotificacoes() {
        NotificationCenter.clear()
    }
}

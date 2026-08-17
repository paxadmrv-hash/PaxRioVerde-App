package com.example.paxrioverde.util

import androidx.compose.runtime.mutableStateListOf
import com.example.paxrioverde.ui.notifications.NotificationItem
import com.example.paxrioverde.ui.notifications.NotificationType
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * NotificationManager: Gerenciador central de notificações com persistência.
 * Senior Note: Garante que o histórico de mensagens não seja perdido ao fechar o app.
 */
class NotificationManager(private val sessionManager: SessionManager) {
    
    private val _notifications = mutableStateListOf<NotificationItem>()
    val notifications: List<NotificationItem> = _notifications

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        try {
            val json = sessionManager.getSavedNotificationsJson()
            if (json.isNotEmpty()) {
                val saved = Json.decodeFromString<List<NotificationItem>>(json)
                _notifications.clear()
                _notifications.addAll(saved)
            }
            
            if (_notifications.isEmpty()) {
                addNotification(
                    title = "Bem-vindo!",
                    message = "Obrigado por utilizar o aplicativo Pax Rio Verde. Aqui você encontrará todas as novidades e avisos importantes.",
                    type = NotificationType.SYSTEM,
                    isInitial = true
                )
            }
        } catch (e: Exception) {
            PaxLogger.e("Erro ao carregar notificações", e, "NotificationManager")
        }
    }

    private fun saveNotifications() {
        try {
            val json = Json.encodeToString(_notifications.toList())
            sessionManager.saveNotificationsJson(json)
        } catch (e: Exception) {
            PaxLogger.e("Erro ao salvar notificações", e, "NotificationManager")
        }
    }

    fun addNotification(title: String, message: String, type: NotificationType, isInitial: Boolean = false) {
        val id = (_notifications.maxOfOrNull { it.id } ?: 0) + 1
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val timeStr = "${now.dayOfMonth.toString().padStart(2, '0')}/${now.monthNumber.toString().padStart(2, '0')} ${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
        
        _notifications.add(0, NotificationItem(id, title, message, timeStr, type, isRead = false))
        
        if (!isInitial) {
            saveNotifications()
        }
    }

    fun markAsRead(id: Int) {
        val index = _notifications.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = _notifications[index]
            _notifications[index] = item.copy(isRead = true)
            saveNotifications()
        }
    }

    fun removeNotification(id: Int) {
        _notifications.removeAll { it.id == id }
        saveNotifications()
    }

    fun clearAll() {
        _notifications.clear()
        saveNotifications()
    }
}

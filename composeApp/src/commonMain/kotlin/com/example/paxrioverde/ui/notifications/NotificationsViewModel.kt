package com.example.paxrioverde.ui.notifications

import androidx.lifecycle.ViewModel
import com.example.paxrioverde.util.NotificationManager

/**
 * NotificationsViewModel: Gerencia a UI da tela de notificações.
 * Senior Note: Utiliza o NotificationManager para persistência e lógica de negócio.
 */
class NotificationsViewModel(
    private val notificationManager: NotificationManager
) : ViewModel() {

    val notifications: List<NotificationItem> = notificationManager.notifications

    fun marcarComoLida(id: Int) {
        notificationManager.markAsRead(id)
    }

    fun removerNotificacao(id: Int) {
        notificationManager.removeNotification(id)
    }

    fun limparTudo() {
        notificationManager.clearAll()
    }
}

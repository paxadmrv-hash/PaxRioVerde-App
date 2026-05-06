package com.example.paxrioverde.util

import kotlinx.datetime.*

object BillingNotificationManager {
    fun scheduleBillingNotifications(dueDateStr: String) {
        if (dueDateStr.isEmpty() || dueDateStr == "--/--/----") return

        try {
            val scheduler = getNotificationScheduler()
            
            // Format: DD/MM/YYYY
            val parts = dueDateStr.split("/")
            if (parts.size != 3) return
            
            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()
            
            val dueDate = LocalDate(year, month, day)
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            
            if (dueDate < today) return

            // Notification 1: 5 days before
            val fiveDaysBefore = dueDate.minus(5, DateTimeUnit.DAY)
            if (fiveDaysBefore >= today) {
                val triggerTime = LocalDateTime(fiveDaysBefore.year, fiveDaysBefore.month, fiveDaysBefore.dayOfMonth, 9, 0)
                    .toInstant(TimeZone.currentSystemDefault())
                
                scheduler.scheduleNotification(
                    id = 101,
                    title = "Pax Rio Verde",
                    message = "Olá! O vencimento da sua mensalidade do plano Pax Rio Verde é em 5 dias. Efetue o pagamento com toda tranquilidade via aplicativo, assim mantemos tudo em dia sem nenhuma preocupação. 😊",
                    epochSeconds = triggerTime.epochSeconds
                )
            }

            // Notification 2: On the due date
            val triggerTimeToday = LocalDateTime(dueDate.year, dueDate.month, dueDate.dayOfMonth, 8, 30)
                .toInstant(TimeZone.currentSystemDefault())
            
            if (dueDate >= today) {
                scheduler.scheduleNotification(
                    id = 102,
                    title = "Pax Rio Verde",
                    message = "Olá! Sua mensalidade do plano Pax Rio Verde vence hoje. Efetue o pagamento com tranquilidade via aplicativo para evitar interrupções e manter o seu plano em dia.",
                    epochSeconds = triggerTimeToday.epochSeconds
                )
            }
        } catch (e: Exception) {
            println("Error scheduling notifications: ${e.message}")
        }
    }
}

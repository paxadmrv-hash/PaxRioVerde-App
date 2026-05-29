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

            // Overdue Notifications (After due date)
            
            // Notification 3: 5 days overdue
            val fiveDaysAfter = dueDate.plus(5, DateTimeUnit.DAY)
            if (fiveDaysAfter >= today) {
                val triggerTime = LocalDateTime(fiveDaysAfter.year, fiveDaysAfter.month, fiveDaysAfter.dayOfMonth, 10, 0)
                    .toInstant(TimeZone.currentSystemDefault())
                
                scheduler.scheduleNotification(
                    id = 103,
                    title = "Pax Rio Verde",
                    message = "Sua mensalidade está em atraso há 5 dias. Regularize agora e mantenha seus benefícios ativos.",
                    epochSeconds = triggerTime.epochSeconds
                )
            }

            // Notification 4: 15 days overdue
            val fifteenDaysAfter = dueDate.plus(15, DateTimeUnit.DAY)
            if (fifteenDaysAfter >= today) {
                val triggerTime = LocalDateTime(fifteenDaysAfter.year, fifteenDaysAfter.month, fifteenDaysAfter.dayOfMonth, 10, 0)
                    .toInstant(TimeZone.currentSystemDefault())
                
                scheduler.scheduleNotification(
                    id = 104,
                    title = "Pax Rio Verde",
                    message = "Atenção: sua mensalidade está em atraso há 15 dias. Evite a suspensão dos serviços, faça o pagamento hoje mesmo.",
                    epochSeconds = triggerTime.epochSeconds
                )
            }

            // Notification 5: 30 days overdue
            val thirtyDaysAfter = dueDate.plus(30, DateTimeUnit.DAY)
            if (thirtyDaysAfter >= today) {
                val triggerTime = LocalDateTime(thirtyDaysAfter.year, thirtyDaysAfter.month, thirtyDaysAfter.dayOfMonth, 10, 0)
                    .toInstant(TimeZone.currentSystemDefault())
                
                scheduler.scheduleNotification(
                    id = 105,
                    title = "Pax Rio Verde",
                    message = "Importante: sua mensalidade da Pax Rio Verde está em atraso há 30 dias. Para manter seus benefícios ativos, pedimos que faça a regularização o quanto antes.",
                    epochSeconds = triggerTime.epochSeconds
                )
            }
        } catch (e: Exception) {
            println("Error scheduling notifications: ${e.message}")
        }
    }
}

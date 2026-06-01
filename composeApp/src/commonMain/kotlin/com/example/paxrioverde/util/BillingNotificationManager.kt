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
                    message = "Você tem uma mensalidade pendente. Consulte seu plano pelo aplicativo Pax Rio Verde.",
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
                    message = " Identificamos mensalidade em atraso. Regularize facilmente pelo aplicativo Pax Rio Verde.",
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
                    message = "Queremos ajudar você a manter seu plano ativo. Consulte e regularize pelo aplicativo Pax Rio Verde.",
                    epochSeconds = triggerTime.epochSeconds
                )
            }

            // Informative Notifications (Every 10 days: 10, 20, 30)
            listOf(10, 20, 30).forEachIndexed { index, dayOfMonth ->
                try {
                    var scheduledDate = LocalDate(today.year, today.month, dayOfMonth)
                    if (today.dayOfMonth > dayOfMonth) {
                        scheduledDate = scheduledDate.plus(1, DateTimeUnit.MONTH).let {
                            LocalDate(it.year, it.month, dayOfMonth)
                        }
                    }

                    val triggerTime = LocalDateTime(scheduledDate.year, scheduledDate.month, scheduledDate.dayOfMonth, 11, 0)
                        .toInstant(TimeZone.currentSystemDefault())

                    scheduler.scheduleNotification(
                        id = 200 + index,
                        title = "Pax Rio Verde",
                        message = "Você sabia? Pelo aplicativo Pax Rio Verde você pode consultar mensalidades, cartão digital e informações do seu plano a qualquer momento.",
                        epochSeconds = triggerTime.epochSeconds
                    )
                } catch (e: Exception) {
                    // Ignore if the day (e.g., 30th) doesn't exist in the current/next month
                }
            }
        } catch (e: Exception) {
            println("Error scheduling notifications: ${e.message}")
        }
    }
}

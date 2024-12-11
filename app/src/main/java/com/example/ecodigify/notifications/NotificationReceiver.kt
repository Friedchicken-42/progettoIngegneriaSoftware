package com.example.ecodigify.notifications

import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.LocalDateTime

class NotificationReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationScheduler = NotificationScheduler(context)
        val ingredientName = intent.getStringExtra("ingredient_name")!!
        // Convert expirationDate String -> LocalDate
        val ingredientExpirationDate =
            LocalDate.parse(intent.getStringExtra("ingredient_expiration_date"))

        notificationScheduler.sendExpireNotificationFor(ingredientName, ingredientExpirationDate)
    }
}
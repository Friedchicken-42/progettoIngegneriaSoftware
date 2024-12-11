package com.example.ecodigify.notifications

import android.content.Context
import android.content.Intent
import com.example.ecodigify.Manager

class NotificationReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationScheduler = NotificationScheduler(context)
        val ingredientName = intent.getStringExtra("ingredient_name")
        notificationScheduler.sendNotification(ingredientName!!)
    }
}
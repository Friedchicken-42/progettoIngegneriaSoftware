package com.example.ecodigify.notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ecodigify.dataclass.Ingredient
import java.util.TimeZone

class NotificationScheduler(private val context: Context) {
    init {
        createNotificationChannel()
    }

    // Schedule a notification for a specific LocalDate (on that date at midnight)
    fun scheduleNotificationFor(ingredient: Ingredient) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationTimeMillis = ingredient.expirationDate
            .atStartOfDay()
            .atZone(TimeZone.getDefault().toZoneId())
            .toInstant()
            .toEpochMilli()

        // Create a PendingIntent to trigger the notification
        val intent = Intent(context, NotificationReceiver::class.java)
        // Put indredient name in intent
        intent.putExtra("ingredient_name", ingredient.name)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ingredient.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Set the alarm to trigger at the specified date
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTimeMillis, pendingIntent)

        Log.d(
            "NotificationScheduler",
            "Notification scheduled for ${ingredient.name} at ${ingredient.expirationDate} midnight"
        )
    }

    // Create the Notification Channel (Required for Android 8.0 and above)
    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Scheduled Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleNotificationForEach(ingredients: List<Ingredient>) {
        ingredients.forEach(::scheduleNotificationFor)
    }

    // Function to create and send a notification
    fun sendNotification(ingredientName: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("Expiration Notice")
            .setContentText("$ingredientName is about to going bad!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }

    companion object {
        private const val notificationChannelId = "notification_channel"
    }
}
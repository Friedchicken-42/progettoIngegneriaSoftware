package com.scratchdevs.ecodigify.notifications

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
import com.scratchdevs.ecodigify.R
import com.scratchdevs.ecodigify.dataclass.Ingredient
import java.time.LocalDate
import java.util.TimeZone

class NotificationScheduler(private val context: Context) {
    init {
        createNotificationChannel()
    }

    /**
     * Set expire notification at about 5 days before the expiration date
     *
     * @param ingredient to process
     * @return if the notification was set or not
     */
    fun setExpireNotificationFor(ingredient: Ingredient): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!shouldNotifyFor(ingredient))
            return false

        val notificationTimeMillis = ingredient.expirationDate
            .minusDays(7)
            .atStartOfDay()
            .atZone(TimeZone.getDefault().toZoneId())
            .toInstant()
            .toEpochMilli()

        // Create a PendingIntent to trigger the notification
        val intent = Intent(context, NotificationReceiver::class.java)

        // Put ingredient metadata into the intent so we can retrieve when we receive the intent (see `NotificationReceiever`)
        intent.putExtra("ingredient_name", ingredient.name)
        intent.putExtra("ingredient_expiration_date", ingredient.expirationDate.toString())

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            // FIXME: This can cause issues due to the long -> int cast
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

        return true
    }

    /**
     * Send expiration notification for `ingredientName`
     *
     * @param ingredientName name of ingredient that is about to expire
     * @param ingredientExpirationDate actual expiration date of the ingredient
     */
    fun sendExpireNotificationFor(ingredientName: String, ingredientExpirationDate: LocalDate) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle(context.getString(R.string.notification_title))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        ingredientExpirationDate.compareTo(LocalDate.now()).let {
            notification.setContentText(
                when {
                    it < 0 -> context.getString(R.string.after_expiration_date_msg, ingredientName)
                    it == 0 -> context.getString(R.string.on_expiration_date_msg, ingredientName)
                    else -> context.getString(R.string.before_expiration_date_msg, ingredientName)
                }
            )
        }

        notificationManager.notify(1, notification.build())
    }

    private fun shouldNotifyFor(ingredient: Ingredient): Boolean {
        val daysUntilExpiration = ingredient.expirationDate.compareTo(LocalDate.now())
        val daysFromLastNotification = ingredient.lastNotified?.compareTo(ingredient.expirationDate)
        val hasNotified = ingredient.lastNotified != null

        println(daysFromLastNotification)
        println(daysUntilExpiration)

        return when {
            hasNotified && daysUntilExpiration < 0 && daysFromLastNotification!! <= 0 -> true
            hasNotified && daysUntilExpiration == 0 && daysFromLastNotification!! < 0 -> true
            !hasNotified && daysUntilExpiration <= 7 -> true
            else -> false
        }
    }

    // Create the Notification Channel (Required for Android 8.0 and above)
    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Scheduled Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "notification_channel"
    }
}

package com.example.pstudy.data.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.pstudy.R
import java.util.Calendar

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "reminder_channel"
        const val REMINDER_REQUEST_CODE = 123
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_NOTIFICATION_CONTENT = "notification_content"
        const val EXTRA_REPEAT_DAILY = "repeat_daily"
        private const val TAG = "NotificationHelper"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleReminder(
        notificationId: Int,
        content: String,
        timeInMillis: Long,
        repeatDaily: Boolean
    ): Boolean {
        if (!isValidReminderTime(timeInMillis)) {
            Log.w(TAG, "Invalid reminder time: $timeInMillis")
            return false
        }

        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Log.w(TAG, "Cannot schedule exact alarms, permission not granted")
                return false
            }

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                putExtra(EXTRA_NOTIFICATION_CONTENT, content)
                putExtra(EXTRA_REPEAT_DAILY, repeatDaily)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REMINDER_REQUEST_CODE + notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )

            return true
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when scheduling alarm: ${e.message}")
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule reminder", e)
            return false
        }
    }

    fun cancelReminder(notificationId: Int) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REMINDER_REQUEST_CODE + notificationId,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            ) ?: return

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel reminder", e)
        }
    }

    fun isValidReminderTime(timeInMillis: Long): Boolean {
        val currentTime = Calendar.getInstance().timeInMillis
        return timeInMillis > currentTime
    }
}
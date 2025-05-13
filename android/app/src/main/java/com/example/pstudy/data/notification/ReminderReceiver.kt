package com.example.pstudy.data.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.pstudy.R
import com.example.pstudy.view.home.HomeActivity
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(NotificationHelper.EXTRA_NOTIFICATION_ID, 1)
        val content = intent.getStringExtra(NotificationHelper.EXTRA_NOTIFICATION_CONTENT) ?: ""
        val repeatDaily = intent.getBooleanExtra(NotificationHelper.EXTRA_REPEAT_DAILY, false)

        showNotification(context, notificationId, content)
        
        if (repeatDaily) {
            scheduleTomorrowReminder(context, notificationId, content)
        }
    }
    
    private fun showNotification(context: Context, notificationId: Int, content: String) {
        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val flags =
            PendingIntent.FLAG_IMMUTABLE

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, flags)
        
        val builder = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }
    
    private fun scheduleTomorrowReminder(context: Context, notificationId: Int, content: String) {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1) // Add 1 day
        }
        
        val helper = NotificationHelper(context)
        helper.scheduleReminder(
            notificationId,
            content,
            calendar.timeInMillis,
            true // Keep it repeating daily
        )
    }
}
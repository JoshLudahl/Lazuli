package com.softklass.lazuli.data.device

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.softklass.lazuli.MainActivity
import com.softklass.lazuli.R

class ReminderReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val itemId = intent.getIntExtra(ReminderScheduler.EXTRA_ITEM_ID, -1)
        val title = intent.getStringExtra(ReminderScheduler.EXTRA_ITEM_TITLE) ?: context.getString(R.string.app_name)

        if (itemId <= 0) return

        val channelId = NotificationChannels.REMINDERS
        ensureChannel(context, channelId)

        val openIntent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(ReminderScheduler.EXTRA_ITEM_ID, itemId)
            }
        val contentPi =
            PendingIntent.getActivity(
                context,
                itemId,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notification =
            NotificationCompat
                .Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Reminder")
                .setContentText(title)
                .setAutoCancel(true)
                .setContentIntent(contentPi)
                .build()

        NotificationManagerCompat.from(context).notify(itemId, notification)
    }

    private fun ensureChannel(
        context: Context,
        channelId: String,
    ) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(channelId, "Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(channel)
        }
    }
}

object NotificationChannels {
    const val REMINDERS = "reminders"
}

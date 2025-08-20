package com.softklass.lazuli.data.device

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

object ReminderScheduler {
    const val EXTRA_ITEM_ID = "EXTRA_ITEM_ID"
    const val EXTRA_ITEM_TITLE = "EXTRA_ITEM_TITLE"

    fun scheduleReminder(
        context: Context,
        itemId: Int,
        title: String,
        triggerAtMillis: Long,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = pendingIntent(context, itemId, title)
        // Cancel any existing alarm for this item
        alarmManager.cancel(pi)

        val canExact =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true // exact allowed below Android 12
            }

        Log.i("ReminderScheduler", "Scheduling reminder for item $itemId with title $title at $triggerAtMillis")

        if (canExact) {
            // Use exact when permitted
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pi,
            )
        } else {
            // Fallback to inexact alarm; OS may delay slightly but reminder will still fire
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pi,
            )
        }
    }

    fun cancelReminder(
        context: Context,
        itemId: Int,
        title: String = "",
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = pendingIntent(context, itemId, title)
        alarmManager.cancel(pi)
    }

    private fun pendingIntent(
        context: Context,
        itemId: Int,
        title: String,
    ): PendingIntent {
        val intent =
            Intent(context, ReminderReceiver::class.java).apply {
                putExtra(EXTRA_ITEM_ID, itemId)
                putExtra(EXTRA_ITEM_TITLE, title)
            }
        return PendingIntent.getBroadcast(
            context,
            itemId, // unique per item
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}

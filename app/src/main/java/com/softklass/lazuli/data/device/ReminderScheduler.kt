package com.softklass.lazuli.data.device

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission

object ReminderScheduler {
    const val EXTRA_ITEM_ID = "EXTRA_ITEM_ID"
    const val EXTRA_ITEM_TITLE = "EXTRA_ITEM_TITLE"

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
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
        // Schedule exact alarm; if exact alarms are restricted, system may approximate
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pi,
        )
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

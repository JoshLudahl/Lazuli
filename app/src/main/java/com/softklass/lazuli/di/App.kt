package com.softklass.lazuli.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            val channelId = com.softklass.lazuli.data.device.NotificationChannels.REMINDERS
            if (nm.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(channelId, "Reminders", NotificationManager.IMPORTANCE_DEFAULT)
                nm.createNotificationChannel(channel)
            }
        }
    }
}

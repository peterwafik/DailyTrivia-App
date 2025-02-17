package com.example.dailytrivia

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

import java.util.*


object NotificationUtils {
    const val CHANNEL_ID = "daily_quiz_channel"
    const val CHANNEL_NAME = "Daily Quiz Notifications"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for daily quiz reminders"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }


    }
}

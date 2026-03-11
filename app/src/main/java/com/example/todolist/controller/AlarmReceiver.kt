package com.example.todolist.controller

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Tâche"
        val isLate = intent.getBooleanExtra("IS_LATE", false)
        val priorityName = intent.getStringExtra("PRIORITY")

        val priorityEmoji = when (priorityName) {
            "CRITICAL" -> "🔴 "
            "IMPORTANT" -> "🟠 "
            "ROUTINE" -> "🟡 "
            "SOMEDAY" -> "🟢 "
            else -> ""
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_alarms"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarmes de tâches",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val message = if (isLate) {
            "${priorityEmoji}La tâche \"$taskTitle\" est en retard d'un jour !"
        } else {
            "${priorityEmoji}C'est l'heure de faire : \"$taskTitle\""
        }

        val notificationTitle = "${priorityEmoji}Rappel de tâche"

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(notificationTitle)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(taskTitle.hashCode(), notification)
    }
}

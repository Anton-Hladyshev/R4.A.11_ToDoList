package com.example.todolist.controller

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.example.todolist.model.Task
import java.util.Calendar

class AlarmController(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleTaskAlarm(task: Task) {
        val calendar = task.deadline.clone() as Calendar

        // Alarme pour le moment où la tâche doit être faite
        scheduleAlarm(task, calendar.timeInMillis, false)

        // Alarme pour le retard de 1 jour
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        scheduleAlarm(task, calendar.timeInMillis, true)
    }

    private fun scheduleAlarm(task: Task, timeInMillis: Long, isLate: Boolean) {
        if (timeInMillis < System.currentTimeMillis()) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TASK_TITLE", task.title)
            putExtra("IS_LATE", isLate)
            putExtra("PRIORITY", task.priority?.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode() + (if (isLate) 1 else 0),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        } else {
            // Repli sur une alarme non exacte si la permission n'est pas accordée
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            // Optionnel : Rediriger l'utilisateur vers les paramètres
            // requestExactAlarmPermission()
        }
    }

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun cancelAlarms(task: Task) {
        val intent = Intent(context, AlarmReceiver::class.java)
        
        val p1 = PendingIntent.getBroadcast(
            context, task.id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val p2 = PendingIntent.getBroadcast(
            context, task.id.hashCode() + 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(p1)
        alarmManager.cancel(p2)
    }
}

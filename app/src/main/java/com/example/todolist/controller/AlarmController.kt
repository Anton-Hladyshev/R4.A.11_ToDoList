package com.example.todolist.controller

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.todolist.model.Task
import java.util.Calendar

class AlarmController(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleTaskAlarm(task: Task) {
        val calendar = Calendar.getInstance()
        
        // On combine la date et l'heure de la tâche
        val taskDate = Calendar.getInstance().apply { time = task.endDate }
        val taskTime = Calendar.getInstance().apply { time = task.endTime }
        
        calendar.set(
            taskDate.get(Calendar.YEAR),
            taskDate.get(Calendar.MONTH),
            taskDate.get(Calendar.DAY_OF_MONTH),
            taskTime.get(Calendar.HOUR_OF_DAY),
            taskTime.get(Calendar.MINUTE),
            0
        )

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
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode() + (if (isLate) 1 else 0),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
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

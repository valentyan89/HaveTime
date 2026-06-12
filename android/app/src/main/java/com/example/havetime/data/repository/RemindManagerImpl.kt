package com.example.havetime.data.repository

import android.content.Context
import com.example.havetime.domain.repository.RemindManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import com.example.havetime.data.broadcast_receiver.RemindReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemindManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) : RemindManager {
    override fun scheduleRemind(
        taskId: Int,
        taskTitle: String,
        startTimeMillis: Long
    ) {
        if (startTimeMillis - System.currentTimeMillis() < 60 * 60 * 1000) return

        val remindTime = startTimeMillis - 60*60*1000

        val intent = Intent(context, RemindReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
            putExtra("TASK_TITLE", taskTitle)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            remindTime,
            pendingIntent
        )
    }

    override fun cancelRemind(taskId: Int) {
        val intent = Intent(context, RemindReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
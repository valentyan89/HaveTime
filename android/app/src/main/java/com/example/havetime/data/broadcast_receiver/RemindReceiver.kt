package com.example.havetime.data.broadcast_receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.glance.appwidget.updateAll
import com.example.havetime.MainActivity
import com.example.havetime.presentation.widget.HaveTimeWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RemindReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("TASK_ID", 0)
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Предстоящая задача"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "application_reminder"

        val intentActivity = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_TASK_ID", taskId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            intentActivity,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Напоминание об активности")
            .setContentText("Через час: $taskTitle")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(taskId, notification)

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                HaveTimeWidget().updateAll(context.applicationContext)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
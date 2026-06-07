package com.example.havetime.domain.repository

interface RemindManager {
    fun scheduleRemind(taskId: Int, taskTitle: String, startTimeMillis: Long)
    fun cancelRemind(taskId: Int)
}
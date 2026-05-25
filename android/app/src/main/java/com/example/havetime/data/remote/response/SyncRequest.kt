package com.example.havetime.data.remote.response

import com.example.havetime.data.model.activity.ActivityDto
import kotlinx.serialization.Serializable

@Serializable
data class SyncRequest(
    val activities: List<ActivityDto>,
    val lastSync: Long
)

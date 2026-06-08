package com.example.havetime.data.remote.response

import com.example.havetime.data.model.activity.ActivityNetworkDto
import kotlinx.serialization.Serializable

@Serializable
data class SyncRequest(
    val activities: List<ActivityNetworkDto>,
    val lastSync: Long
)

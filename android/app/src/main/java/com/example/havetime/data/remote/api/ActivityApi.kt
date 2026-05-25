package com.example.havetime.data.remote.api

import com.example.havetime.data.model.activity.ActivityDto
import com.example.havetime.data.remote.response.SyncRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

class ActivityApi(private val client: HttpClient) {
    suspend fun getActivities(): List<ActivityDto> {
        return client.get("/activities").body()
    }

    suspend fun sync(syncRequest: SyncRequest): List<ActivityDto>{
        return client.post("/activities/sync") {
            setBody(syncRequest)
        }.body()
    }

    suspend fun deleteActivity(id: Int): Boolean {
        val response = client.delete("activities/$id")
        return response.status == HttpStatusCode.OK
    }
}
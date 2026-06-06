package com.example.havetime.data.remote.api

import com.example.havetime.data.remote.request.LoginRequest
import com.example.havetime.data.remote.request.RegisterRequest
import com.example.havetime.data.remote.response.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(private val client: HttpClient) {
    suspend fun login(request: LoginRequest): LoginResponse = 
        client.post("/login") { 
            contentType(ContentType.Application.Json)
            setBody(request) 
        }.body()

    suspend fun register(request: RegisterRequest): LoginResponse = 
        client.post("/register") { 
            contentType(ContentType.Application.Json)
            setBody(request) 
        }.body()
}
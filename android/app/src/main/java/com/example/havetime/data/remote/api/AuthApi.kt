package com.example.havetime.data.remote.api

import com.example.havetime.data.remote.response.LoginRequest
import com.example.havetime.data.remote.response.LoginResponse
import com.example.havetime.data.remote.client.KtorClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthApi(private val client: HttpClient) {
    suspend fun login(login: String, password: String): LoginResponse{
        return client.post("/login"){
            setBody(LoginRequest(login, password))
        }.body()
    }
}
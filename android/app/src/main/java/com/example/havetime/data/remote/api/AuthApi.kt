package com.example.havetime.data.remote.api

import com.example.havetime.data.remote.response.LoginRequest
import com.example.havetime.data.remote.response.LoginResponse
import com.example.havetime.data.remote.client.KtorClient
import com.example.havetime.data.remote.response.RegisterRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.contentType

class AuthApi(private val client: HttpClient) {
    suspend fun login(login: String, password: String): LoginResponse{
        return client.post("/login"){
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody(LoginRequest(login, password))
        }.body()
    }

    suspend fun register(login: String, password: String): LoginResponse{
        client.post("/register"){
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody(RegisterRequest(login, password))
        }

        return login(login, password)
    }
}
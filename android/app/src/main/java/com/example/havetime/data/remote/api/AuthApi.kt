package com.example.havetime.data.remote.api

import com.example.havetime.data.remote.response.LoginRequest
import com.example.havetime.data.remote.response.LoginResponse
import com.example.havetime.data.remote.response.RegisterRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthApi @Inject constructor(private val client: HttpClient) {
    suspend fun login(login: String, password: String): LoginResponse{
        val response =  client.post("/login"){
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody(LoginRequest(login, password))
        }

        if (response.status == HttpStatusCode.OK) {
            return response.body<LoginResponse>()
        } else {
            throw ResponseException(response, "Не авторизован")
        }
    }

    suspend fun register(login: String, password: String): LoginResponse{
        val response = client.post("/register"){
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody(RegisterRequest(login, password))
        }

        if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
            return login(login, password)
        } else {
            throw ResponseException(response, "Ошибка сервера ${response.status}")
        }
    }
}
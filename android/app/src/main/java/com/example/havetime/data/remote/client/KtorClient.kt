package com.example.havetime.data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient{
    private var currentAccessToken: String? = null

    val client: HttpClient = HttpClient{
        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
                encodeDefaults = true
            })
        }

        install(Logging) {
            level = LogLevel.ALL
        }

//        install("TokenInterceptor") {
//            requestPipeline.intercept(HttpRequestPipeline.State) {
//                currentAccessToken?.let { token ->
//                    context.header("Authorization", "Bearer $token")
//                }
//            }
//        }

        install(Auth) {
            bearer {
                sendWithoutRequest { true }
                loadTokens {
                    currentAccessToken?.let { BearerTokens(it, "") }
                }
            }
        }

        install(HttpTimeout){
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 10000
        }

        defaultRequest {
            url("http://10.0.2.2:8080/")
            contentType(ContentType.Application.Json)
        }
    }

    fun updateToken(token: String){
        currentAccessToken = token
    }

    fun clearToken(){
        currentAccessToken = null
    }
}
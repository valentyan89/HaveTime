package com.example.havetime.data.remote.client

import com.example.havetime.util.Constants
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {
    private var currentAccessToken: String? = null

    val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
                encodeDefaults = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }

        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }

        install(Logging) {
            level = LogLevel.ALL
        }

        install(Auth) {
            bearer {
                sendWithoutRequest { true }
                loadTokens {
                    currentAccessToken?.let { BearerTokens(it, "") }
                }
            }
        }

        defaultRequest {
            url(Constants.BASE_URL)
        }
    }

    fun updateToken(token: String) {
        currentAccessToken = token
    }

    fun clearToken() {
        currentAccessToken = null
    }
}

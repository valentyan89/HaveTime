package com.example.havetime.di

import com.example.havetime.data.local.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(tokenManager: TokenManager): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
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

            install(Auth) {
                bearer {
                    sendWithoutRequest { true }
                    loadTokens {
                        val token = tokenManager.tokenFlow.firstOrNull()
                        if (!token.isNullOrEmpty()) {
                            BearerTokens(token, "")
                        } else {
                            null
                        }
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 10000
                connectTimeoutMillis = 10000
            }

            defaultRequest {
                url("http://10.0.2.2:8080/")
                contentType(ContentType.Application.Json)
            }
        }
    }
}
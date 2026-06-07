package com.example.havetime.domain.repository

import com.example.havetime.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(login: String, password: String): Result<Unit>
    suspend fun register(login: String, password: String): Result<Unit>
    suspend fun logout()
    fun getUser(): Flow<User?>
    fun isAuthorized(): Flow<Boolean>
}
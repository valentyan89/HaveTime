package com.example.havetime.domain.repository

import com.example.havetime.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User?>
    suspend fun login(login: String, password: String): Result<Unit>
    suspend fun register(login: String, password: String): Result<Unit>
    suspend fun logout()
}

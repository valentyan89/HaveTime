package com.example.havetime.data.repository

import com.example.havetime.domain.repository.UserRepository
import com.example.havetime.data.local.dao.UserDao
import com.example.havetime.data.mapper.toDomain
import com.example.havetime.data.mapper.toEntity
import com.example.havetime.domain.model.User
import com.example.havetime.data.remote.api.AuthApi
import com.example.havetime.data.remote.request.LoginRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val api: AuthApi,
    private val userDao: UserDao
) : UserRepository {
    
    override fun getUser(): Flow<User?> {
        return userDao.getUser().map { it?.toDomain() }
    }

    override suspend fun login(login: String, password: String): Result<Unit> {
        return try {
            val response = api.login(LoginRequest(login, password))
            val user = response.toDomain()
            userDao.insert(user.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        userDao.logout()
    }
}
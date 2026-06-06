package com.example.havetime.data.repository

import com.example.havetime.data.local.TokenManager
import com.example.havetime.data.local.dao.UserDao
import com.example.havetime.data.local.entity.UserEntity
import com.example.havetime.data.remote.api.AuthApi
import com.example.havetime.data.remote.client.KtorClient
import com.example.havetime.data.remote.response.LoginResponse
import com.example.havetime.domain.model.User
import com.example.havetime.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val api: AuthApi,
    private val tokenManager: TokenManager
) : UserRepository {
    override suspend fun login(login: String, password: String): Result<Unit> = runCatching {
        val response: LoginResponse = api.login(login, password)

        tokenManager.saveToken(response.token)
        KtorClient.updateToken(response.token)

        userDao.insert(
            UserEntity(
                serverId = response.id.toIntOrNull() ?: 0,
                login = response.login,
                token = response.token,
                createdAt = System.currentTimeMillis(),
                lastSyncAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun register(login: String, password: String): Result<Unit> = runCatching {
        val response: LoginResponse = api.register(login, password)

        tokenManager.saveToken(response.token)
        KtorClient.updateToken(response.token)

        userDao.insert(
            UserEntity(
                serverId = response.id.toIntOrNull() ?: 0,
                login = response.login,
                token = response.token,
                createdAt = System.currentTimeMillis(),
                lastSyncAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun logout() {
        tokenManager.clearToken()
        KtorClient.clearToken()
        userDao.logout()
    }

    override fun getUser(): Flow<User?> {
        return userDao.getUser().map { entity ->
            entity?.let {
                User(
                    id = it.serverId,
                    login = it.login,
                    token = it.token,
                    createdAt = it.createdAt
                )
            }
        }
    }
}

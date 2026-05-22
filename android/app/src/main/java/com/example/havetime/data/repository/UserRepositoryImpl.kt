package com.example.havetime.data.repository

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
    private val api: AuthApi
) : UserRepository{
    override suspend fun login(login: String, password: String): Result<Unit> = runCatching {
        val response = api.login(login, password)

        KtorClient.updateToken(response.token)
        userDao.insert(
            UserEntity(
                serverId = response.id,
                login = response.login,
                token = response.token,
                createdAt = System.currentTimeMillis(),
                lastSyncAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun logout() {
        KtorClient.clearToken()
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
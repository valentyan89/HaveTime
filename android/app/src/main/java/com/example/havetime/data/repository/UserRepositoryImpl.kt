package com.example.havetime.data.repository

import com.example.havetime.data.local.TokenManager
import com.example.havetime.data.local.dao.TodoDao
import com.example.havetime.data.local.dao.UserDao
import com.example.havetime.data.local.entity.UserEntity
import com.example.havetime.data.remote.api.AuthApi
import com.example.havetime.data.remote.client.KtorClient
import com.example.havetime.data.remote.response.LoginResponse
import com.example.havetime.domain.model.AuthError
import com.example.havetime.domain.model.User
import com.example.havetime.domain.repository.UserRepository
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import javax.inject.Inject


class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val todoDao: TodoDao,
    private val api: AuthApi,
    private val tokenManager: TokenManager
) : UserRepository{

    override suspend fun login(login: String, password: String): Result<Unit> = try {
        val response: LoginResponse = api.login(login, password)

        tokenManager.saveToken(response.token)
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
        todoDao.setTasksBeforeLogin(response.id)

        response.token
        Result.success(Unit)
    } catch (e: ResponseException) {
        when (e.response.status) {
            HttpStatusCode.Unauthorized -> Result.failure(AuthError.InvalidCredentials)
            else -> Result.failure(AuthError.UnknownError("Код сервера: ${e.response.status.value}"))
        }
    } catch (e: IOException) {
        Result.failure(AuthError.NetworkError)
    } catch (e: Exception) {
        Result.failure(AuthError.UnknownError(e.localizedMessage ?: "Критическая ошибка"))
    }

    override suspend fun register(login: String, password: String): Result<Unit> = try {
        val response: LoginResponse = api.register(login, password)

        tokenManager.saveToken(response.token)
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
        todoDao.setTasksBeforeLogin(response.id)

        response.token
        Result.success(Unit)
    } catch (e: ResponseException) {
        when (e.response.status) {
            HttpStatusCode.BadRequest, HttpStatusCode.Conflict -> Result.failure(AuthError.UserAlreadyExists)
            else -> Result.failure(AuthError.UnknownError("Ошибка сервера ${e.response.status}"))
        }
    } catch (e: IOException) {
        Result.failure(AuthError.NetworkError)
    } catch (e: Exception) {
        Result.failure(AuthError.UnknownError(e.localizedMessage))
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
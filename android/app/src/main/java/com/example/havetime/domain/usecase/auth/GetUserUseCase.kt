package com.example.havetime.domain.usecase.auth

import com.example.havetime.domain.model.User
import com.example.havetime.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserUseCase(private val repository: UserRepository) {
    operator fun invoke(): Flow<User?> = repository.getUser()
}
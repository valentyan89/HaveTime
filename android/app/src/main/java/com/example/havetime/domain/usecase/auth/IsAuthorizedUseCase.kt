package com.example.havetime.domain.usecase.auth

import com.example.havetime.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class IsAuthorizedUseCase(private val repository: UserRepository) {
    operator fun invoke(): Flow<Boolean> = repository.isAuthorized()
}
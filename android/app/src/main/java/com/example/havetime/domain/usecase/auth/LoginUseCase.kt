package com.example.havetime.domain.usecase.auth

import com.example.havetime.domain.repository.UserRepository

class LoginUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(login: String, password: String): Result<Unit> = repository.login(login, password)
}
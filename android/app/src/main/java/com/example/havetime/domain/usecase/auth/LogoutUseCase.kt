package com.example.havetime.domain.usecase.auth

import com.example.havetime.domain.repository.UserRepository

class LogoutUseCase(private val repository: UserRepository) {
    suspend operator fun invoke() = repository.logout()
}
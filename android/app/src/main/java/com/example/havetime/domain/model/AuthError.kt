package com.example.havetime.domain.model

sealed class AuthError(message: String) : Exception(message) {
    object NetworkError : AuthError("Проверьте интернет")
    object InvalidCredentials : AuthError("Неверный логин или пароль")
    object UserAlreadyExists : AuthError("Пользователь с таким логином уже занят")

    data class UnknownError(val errorDetails: String) : AuthError("Ошибка: $errorDetails")
}
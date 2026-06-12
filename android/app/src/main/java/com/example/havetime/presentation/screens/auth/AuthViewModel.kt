package com.example.havetime.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.havetime.domain.model.User
import com.example.havetime.domain.usecase.auth.GetUserUseCase
import com.example.havetime.domain.usecase.auth.LoginUseCase
import com.example.havetime.domain.usecase.auth.LogoutUseCase
import com.example.havetime.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel(){
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser: StateFlow<User?> = getUserUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun login(login: String, password: String){
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = loginUseCase(login, password)
            _authState.value = when {
                result.isSuccess -> AuthState.Success
                else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun register(login: String, password: String){
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = registerUseCase(login, password)
            _authState.value = when {
                result.isSuccess -> AuthState.Success
                else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _authState.value = AuthState.Idle
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
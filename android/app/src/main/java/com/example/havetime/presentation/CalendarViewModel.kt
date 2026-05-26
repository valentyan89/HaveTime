package com.example.havetime.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.havetime.HaveTimeApplication
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.usecase.activity.*
import com.example.havetime.domain.usecase.auth.LogoutUseCase
import com.example.havetime.domain.usecase.auth.IsAuthorizedUseCase
import kotlinx.coroutines.flow.*
import java.time.LocalDate

class CalendarViewModel(
    private val addActivityUseCase: AddTodoUseCase,
    private val deleteActivityUseCase: DeleteTodoUseCase,
    private val getIntervalsForDateUseCase: GetIntervalsForDateUseCase,
    private val getTodosUseCase: GetTodosUseCase,
    private val updateActivityUseCase: UpdateActivityUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val isAuthorizedUseCase: IsAuthorizedUseCase,
    private val syncWithServerUseCase: SyncWithServerUseCase
) : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _mapType = MutableStateFlow(0)
    val mapType: StateFlow<Int> = _mapType.asStateFlow()

    val isAuthorized = isAuthorizedUseCase().stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun onMapTypeChanged(type: Int) {
        _mapType.value = type
    }
    
    val activities: StateFlow<List<Activity>> = _selectedDate
        .flatMapLatest { date ->
            getIntervalsForDateUseCase(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    val allActivities: StateFlow<List<Activity>> = getTodosUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addActivity(activity: Activity) {
        addActivityUseCase(activity).launchIn(viewModelScope)
    }

    fun deleteActivity(id: Int) {
        deleteActivityUseCase(id).launchIn(viewModelScope)
    }

    fun updateActivity(activity: Activity) {
        updateActivityUseCase(activity).launchIn(viewModelScope)
    }

    fun logout() {
        logoutUseCase().launchIn(viewModelScope)
    }

    fun syncWithServer() {
        syncWithServerUseCase().launchIn(viewModelScope)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as HaveTimeApplication)
                val activityRepo = app.activityRepository
                val userRepo = app.userRepository
                
                CalendarViewModel(
                    addActivityUseCase = AddTodoUseCase(activityRepo),
                    deleteActivityUseCase = DeleteTodoUseCase(activityRepo),
                    getIntervalsForDateUseCase = GetIntervalsForDateUseCase(activityRepo),
                    getTodosUseCase = GetTodosUseCase(activityRepo),
                    updateActivityUseCase = UpdateActivityUseCase(activityRepo),
                    syncWithServerUseCase = SyncWithServerUseCase(activityRepo),
                    logoutUseCase = LogoutUseCase(userRepo),
                    isAuthorizedUseCase = IsAuthorizedUseCase(userRepo)
                )
            }
        }
    }
}

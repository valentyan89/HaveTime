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
import com.example.havetime.domain.usecase.date.GetCurrentTimeUseCase
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalDateTime

class CalendarViewModel(
    private val addActivityUseCase: AddActivityUseCase,
    private val deleteActivityUseCase: DeleteTodoUseCase,
    private val getIntervalsForDateUseCase: GetIntervalsForDateUseCase,
    private val getTodosUseCase: GetTodosUseCase,
    private val updateActivityUseCase: UpdateActivityUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val isAuthorizedUseCase: IsAuthorizedUseCase,
    private val getCurrentTimeUseCase: GetCurrentTimeUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _mapType = MutableStateFlow(0)
    val mapType: StateFlow<Int> = _mapType.asStateFlow()

    val isAuthorized = isAuthorizedUseCase().stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val currentTime: StateFlow<LocalDateTime> = getCurrentTimeUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalDateTime.now())

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

    fun deleteActivity(id: String) {
        deleteActivityUseCase(id).launchIn(viewModelScope)
    }

    fun updateActivity(activity: Activity) {
        updateActivityUseCase(activity).launchIn(viewModelScope)
    }

    fun logout() {
        logoutUseCase().launchIn(viewModelScope)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as HaveTimeApplication)
                val di = app.appModule
                CalendarViewModel(
                    addActivityUseCase = di.addActivityUseCase,
                    deleteActivityUseCase = di.deleteActivityUseCase,
                    getIntervalsForDateUseCase = di.getIntervalsForDateUseCase,
                    getTodosUseCase = di.getTodosUseCase,
                    updateActivityUseCase = di.updateActivityUseCase,
                    logoutUseCase = di.logoutUseCase,
                    isAuthorizedUseCase = di.isAuthorizedUseCase,
                    getCurrentTimeUseCase = di.getCurrentTimeUseCase
                )
            }
        }
    }
}
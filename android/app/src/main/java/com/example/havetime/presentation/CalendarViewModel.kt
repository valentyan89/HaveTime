package com.example.havetime.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.havetime.HaveTimeApplication
import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.usecase.activity.AddTodoUseCase
import com.example.havetime.domain.usecase.activity.DeleteTodoUseCase
import com.example.havetime.domain.usecase.activity.GetIntervalsForDateUseCase
import com.example.havetime.domain.usecase.activity.GetTodosUseCase
import com.example.havetime.domain.usecase.activity.UpdateActivityUseCase
import com.example.havetime.domain.usecase.activity.SyncWithServerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class CalendarViewModel(
    private val addTodoUseCase: AddTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val getIntervalsForDateUseCase: GetIntervalsForDateUseCase,
    private val getTodosUseCase: GetTodosUseCase,
    private val updateActivityUseCase: UpdateActivityUseCase,
    private val syncWithServerUseCase: SyncWithServerUseCase
) : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _mapType = MutableStateFlow(0) // 0: Standard
    val mapType: StateFlow<Int> = _mapType.asStateFlow()

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

    fun addActivity(todo: Activity) {
        addTodoUseCase(todo).launchIn(viewModelScope)
    }

    fun deleteActivity(id: String) {
        deleteTodoUseCase(id).launchIn(viewModelScope)
    }

    fun updateActivity(activity: Activity) {
        updateActivityUseCase(activity).launchIn(viewModelScope)
    }

    fun syncWithServer() {
        syncWithServerUseCase().launchIn(viewModelScope)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as HaveTimeApplication)

                val repo = application.activityRepository

                CalendarViewModel(
                    addTodoUseCase = AddTodoUseCase(repo),
                    deleteTodoUseCase = DeleteTodoUseCase(repo),
                    getIntervalsForDateUseCase = GetIntervalsForDateUseCase(repo),
                    getTodosUseCase = GetTodosUseCase(repo),
                    updateActivityUseCase = UpdateActivityUseCase(repo),
                    syncWithServerUseCase = SyncWithServerUseCase(repo)
                )
            }
        }
    }
}
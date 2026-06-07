package com.example.havetime.presentation.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.havetime.HaveTimeApplication
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.usecase.activity.AddTodoUseCase
import com.example.havetime.domain.usecase.activity.DeleteTodoUseCase
import com.example.havetime.domain.usecase.activity.GetIntervalsForDateUseCase
import com.example.havetime.domain.usecase.activity.GetTodosUseCase
import com.example.havetime.domain.usecase.activity.SyncWithServerUseCase
import com.example.havetime.domain.usecase.activity.UpdateActivityUseCase
import com.example.havetime.domain.usecase.date.GetCurrentDateUseCase
import com.example.havetime.domain.usecase.date.GetNextDayUseCase
import com.example.havetime.domain.usecase.date.GetNextWeekUseCase
import com.example.havetime.domain.usecase.date.GetPreviousDayUseCase
import com.example.havetime.domain.usecase.date.GetPreviousWeekUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY

class MapViewModel(
    private val addTodoUseCase: AddTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val updateActivityUseCase: UpdateActivityUseCase,
    private val getTodosUseCase: GetTodosUseCase,
    private val getIntervalsForDateUseCase: GetIntervalsForDateUseCase,
    private val syncWithServerUseCase: SyncWithServerUseCase,
    private val getCurrentDateUseCase: GetCurrentDateUseCase,
    private val getNextWeekUseCase: GetNextWeekUseCase,
    private val getPreviousWeekUseCase: GetPreviousWeekUseCase,
    private val getNextDayUseCase: GetNextDayUseCase,
    private val getPreviousDayUseCase: GetPreviousDayUseCase
) : ViewModel() {
    private val _currentDate = MutableStateFlow<LocalDate?>(null)
    val currentDate: StateFlow<LocalDate?> = _currentDate.asStateFlow()

    init {
        viewModelScope.launch {
            val today = getCurrentDateUseCase().first()
            _currentDate.value = today
        }
    }

    val activityForDate: StateFlow<List<Activity>> = _currentDate
        .flatMapLatest { date ->
            if (date != null) getIntervalsForDateUseCase(date)
            else flowOf(emptyList())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activityForWeek: StateFlow<List<Activity>> = getTodosUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val geoMarksForDate: StateFlow<List<Activity>> = activityForDate.map { activities ->
        activities.filter { it.location != null }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectDate(date: LocalDate) {
        _currentDate.value = date
    }

    fun go2Today(){
        viewModelScope.launch {
            val today = getCurrentDateUseCase().first()
            _currentDate.value = today
        }
    }

    fun go2NextDay() {
        _currentDate.value?.let { currentDate ->
            viewModelScope.launch {
                val nextDay = getNextDayUseCase(currentDate)
                _currentDate.value = nextDay
            }
        }
    }

    fun go2PrevDay() {
        _currentDate.value?.let { currentDate ->
            viewModelScope.launch {
                val previousDay = getPreviousDayUseCase(currentDate)
                _currentDate.value = previousDay
            }
        }
    }

    fun go2NextWeek() {
        _currentDate.value?.let { currentDate ->
            viewModelScope.launch {
                val nextWeek = getNextWeekUseCase(currentDate)
                _currentDate.value = nextWeek
            }
        }
    }

    fun go2PrevWeek() {
        _currentDate.value?.let { currentDate ->
            viewModelScope.launch {
                val previousWeek = getPreviousWeekUseCase(currentDate)
                _currentDate.value = previousWeek
            }
        }
    }

    fun addActivity(activity: Activity) {
        addTodoUseCase(activity).launchIn(viewModelScope)
    }

    fun updateActivity(activity: Activity) {
        updateActivityUseCase(activity).launchIn(viewModelScope)
    }

    fun deleteActivity(id: Int) {
        deleteTodoUseCase(id).launchIn(viewModelScope)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as HaveTimeApplication)
                val activityRepo = application.todoRepository
                val dateRepo = application.dateRepository

                MapViewModel(
                    addTodoUseCase = AddTodoUseCase(activityRepo),
                    deleteTodoUseCase = DeleteTodoUseCase(activityRepo),
                    updateActivityUseCase = UpdateActivityUseCase(activityRepo),
                    getTodosUseCase = GetTodosUseCase(activityRepo),
                    getIntervalsForDateUseCase = GetIntervalsForDateUseCase(activityRepo),
                    syncWithServerUseCase = SyncWithServerUseCase(activityRepo),
                    getCurrentDateUseCase = GetCurrentDateUseCase(dateRepo),
                    getNextWeekUseCase = GetNextWeekUseCase(dateRepo),
                    getPreviousWeekUseCase = GetPreviousWeekUseCase(dateRepo),
                    getNextDayUseCase = GetNextDayUseCase(dateRepo),
                    getPreviousDayUseCase = GetPreviousDayUseCase(dateRepo)
                )
            }
        }
    }
}
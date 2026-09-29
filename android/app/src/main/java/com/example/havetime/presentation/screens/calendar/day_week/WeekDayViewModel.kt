package com.example.havetime.presentation.screens.calendar.day_week

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.usecase.activity.AddTodoUseCase
import com.example.havetime.domain.usecase.activity.DeleteTodoUseCase
import com.example.havetime.domain.usecase.activity.GetIntervalsForDateUseCase
import com.example.havetime.domain.usecase.activity.GetTodosUseCase
import com.example.havetime.domain.usecase.activity.SearchUseCase
import com.example.havetime.domain.usecase.activity.SyncWithServerUseCase
import com.example.havetime.domain.usecase.activity.UpdateActivityUseCase
import com.example.havetime.domain.usecase.date.GetCurrentDateUseCase
import com.example.havetime.domain.usecase.date.GetCurrentTimeUseCase
import com.example.havetime.domain.usecase.date.GetInitialDateTimeUseCase
import com.example.havetime.domain.usecase.date.GetInitialDateUseCase
import com.example.havetime.domain.usecase.date.GetNextDayUseCase
import com.example.havetime.domain.usecase.date.GetNextWeekUseCase
import com.example.havetime.domain.usecase.date.GetPreviousDayUseCase
import com.example.havetime.domain.usecase.date.GetPreviousWeekUseCase
import com.example.havetime.presentation.screens.CalendarMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class WeekDayViewModel @Inject constructor(
    private val addTodoUseCase: AddTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val updateActivityUseCase: UpdateActivityUseCase,
    private val getTodosUseCase: GetTodosUseCase,
    private val getIntervalsForDateUseCase: GetIntervalsForDateUseCase,
    private val syncWithServerUseCase: SyncWithServerUseCase,
    private val getCurrentTimeUseCase: GetCurrentTimeUseCase,
    private val getCurrentDateUseCase: GetCurrentDateUseCase,
    private val getNextWeekUseCase: GetNextWeekUseCase,
    private val getPreviousWeekUseCase: GetPreviousWeekUseCase,
    private val getNextDayUseCase: GetNextDayUseCase,
    private val getPreviousDayUseCase: GetPreviousDayUseCase,
    private val searchUseCase: SearchUseCase,
    private val getInitialDateUseCase: GetInitialDateUseCase,
    private val getInitialDateTimeUseCase: GetInitialDateTimeUseCase
) : ViewModel() {
    private val _currentDate = MutableStateFlow<LocalDate>(getInitialDateUseCase())
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()

    private val _calendarMode = MutableStateFlow<CalendarMode>(CalendarMode.WEEK_DAY)
    val calendarMode: StateFlow<CalendarMode> = _calendarMode.asStateFlow()

    init {
        viewModelScope.launch {
            getCurrentDateUseCase().collect { realToday ->
                if (_currentDate.value == realToday.minusDays(1)) {
                    _currentDate.value = realToday
                }
            }
        }
    }

    val currentTime: StateFlow<LocalDateTime> = getCurrentTimeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = getInitialDateTimeUseCase()
        )

    val today: StateFlow<LocalDate> = getCurrentDateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = getInitialDateUseCase()
        )

    fun setCalendarMode(mode: CalendarMode) {
        _calendarMode.value = mode
    }

    val activityForDate: StateFlow<List<Activity>> = _currentDate
        .flatMapLatest { date ->
            getIntervalsForDateUseCase(date)
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
        _currentDate.value.let { currentDate ->
            viewModelScope.launch {
                val nextDay = getNextDayUseCase(currentDate)
                _currentDate.value = nextDay
            }
        }
    }

    fun go2PrevDay() {
        _currentDate.value.let { currentDate ->
            viewModelScope.launch {
                val previousDay = getPreviousDayUseCase(currentDate)
                _currentDate.value = previousDay
            }
        }
    }

    fun go2NextWeek() {
        _currentDate.value.let { currentDate ->
            viewModelScope.launch {
                val nextWeek = getNextWeekUseCase(currentDate)
                _currentDate.value = nextWeek
            }
        }
    }

    fun go2PrevWeek() {
        _currentDate.value.let { currentDate ->
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

    val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<Activity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList())
            else searchUseCase(query)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}

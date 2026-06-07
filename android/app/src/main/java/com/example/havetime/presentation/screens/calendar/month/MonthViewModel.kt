package com.example.havetime.presentation.screens.calendar.month

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.havetime.HaveTimeApplication
import com.example.havetime.domain.repository.DateRepository
import com.example.havetime.domain.usecase.activity.GetIntervalsForDateUseCase
import com.example.havetime.domain.usecase.activity.GetTodosUseCase
import com.example.havetime.domain.usecase.activity.SearchUseCase
import com.example.havetime.domain.usecase.date.GetCurrentDateUseCase
import com.example.havetime.domain.usecase.date.GetNextMonthUseCase
import com.example.havetime.domain.usecase.date.GetPreviousMonthUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import com.example.havetime.domain.model.Activity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class MonthViewModel(
    private val dateRepository: DateRepository,
    private val getCurrentDateUseCase: GetCurrentDateUseCase,
    private val getNextMonthUseCase: GetNextMonthUseCase,
    private val getPreviousMonthUseCase: GetPreviousMonthUseCase,
    private val getIntervalsForDateUseCase: GetIntervalsForDateUseCase,
    private val getTodosUseCase: GetTodosUseCase,
    private val searchUseCase: SearchUseCase
) : ViewModel() {

    private val _currentMonth = MutableStateFlow<YearMonth?>(null)
    val currentMonth: StateFlow<YearMonth?> = _currentMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    init {
        viewModelScope.launch {
            val today = getCurrentDateUseCase().first()
            _currentMonth.value = YearMonth.from(today)
            _selectedDate.value = today
        }
    }

    val intensityMap: StateFlow<Map<LocalDate, Int>> = _currentMonth
        .flatMapLatest { month ->
            if (month == null) {
                flowOf(emptyMap())
            } else {
                getTodosUseCase().map { allActivities ->
                    val result = mutableMapOf<LocalDate, Int>()
                    var date = dateRepository.getFirstDayOfMonth(month)
                    val lastDate = dateRepository.getLastDayOfMonth(month)

                    while (!date.isAfter(lastDate)) {
                        val dayStart = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                        val dayEnd = date.plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                        
                        val count = allActivities.count { 
                            it.timeInterval.startTime < dayEnd && it.timeInterval.endTime > dayStart
                        }
                        result[date] = count
                        date = dateRepository.getNextDay(date)
                    }
                    result
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun go2Today() {
        viewModelScope.launch {
            val today = getCurrentDateUseCase().first()
            _currentMonth.value = YearMonth.from(today)
            _selectedDate.value = today
        }
    }

    fun go2NextMonth() {
        _currentMonth.value?.let { month ->
            viewModelScope.launch {
                _currentMonth.value = getNextMonthUseCase(month)
            }
        }
    }

    fun go2PrevMonth() {
        _currentMonth.value?.let { month ->
            viewModelScope.launch {
                _currentMonth.value = getPreviousMonthUseCase(month)
            }
        }
    }

    fun setMonth(month: YearMonth) {
        if (_currentMonth.value != month) {
            _currentMonth.value = month
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun getIntensityForDate(date: LocalDate): Int {
        return intensityMap.value[date] ?: 0
    }

    fun selectMonth(yearMonth: YearMonth) {
        _currentMonth.value = yearMonth
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as HaveTimeApplication
                val dateRepo = application.dateRepository
                val activityRepo = application.todoRepository

                MonthViewModel(
                    dateRepository = dateRepo,
                    getCurrentDateUseCase = GetCurrentDateUseCase(dateRepo),
                    getNextMonthUseCase = GetNextMonthUseCase(dateRepo),
                    getPreviousMonthUseCase = GetPreviousMonthUseCase(dateRepo),
                    getIntervalsForDateUseCase = GetIntervalsForDateUseCase(activityRepo),
                    getTodosUseCase = GetTodosUseCase(activityRepo),
                    searchUseCase = SearchUseCase(activityRepo)
                )
            }
        }
    }
}

package com.example.havetime.presentation.screens.calendar.month

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.havetime.HaveTimeApplication
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.repository.DateRepository
import com.example.havetime.domain.usecase.activity.GetIntervalsForDateUseCase
import com.example.havetime.domain.usecase.activity.GetTodosUseCase
import com.example.havetime.domain.usecase.activity.SearchUseCase
import com.example.havetime.domain.usecase.date.GetCurrentDateUseCase
import com.example.havetime.domain.usecase.date.GetInitialDateUseCase
import com.example.havetime.domain.usecase.date.GetNextMonthUseCase
import com.example.havetime.domain.usecase.date.GetPreviousMonthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class MonthViewModel @Inject constructor(
    private val dateRepository: DateRepository,
    private val getCurrentDateUseCase: GetCurrentDateUseCase,
    private val getNextMonthUseCase: GetNextMonthUseCase,
    private val getPreviousMonthUseCase: GetPreviousMonthUseCase,
    private val getIntervalsForDateUseCase: GetIntervalsForDateUseCase,
    private val getTodosUseCase: GetTodosUseCase,
    private val searchUseCase: SearchUseCase,
    private val getInitialDateUseCase: GetInitialDateUseCase
) : ViewModel() {

    private val _currentMonth = MutableStateFlow<YearMonth?>(null)
    val currentMonth: StateFlow<YearMonth?> = _currentMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate>(getInitialDateUseCase())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    init {
        viewModelScope.launch {
            val today = getCurrentDateUseCase().first()
            _currentMonth.value = YearMonth.from(today)
            _selectedDate.value = today
        }
    }

    val intensityMap: StateFlow<Map<LocalDate, Int>> = getTodosUseCase().map { activities ->
            activities.groupBy { activity ->
                    java.time.Instant.ofEpochMilli(activity.timeInterval.startTime).atZone(java.time.ZoneOffset.UTC).toLocalDate()
                }.mapValues { entry -> entry.value.size }
        }.stateIn(
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
}
package com.example.havetime.presentation.screens.calendar.year

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.havetime.domain.usecase.activity.GetActivitiesForMonthUseCase
import com.example.havetime.domain.usecase.date.GetCurrentDateUseCase
import com.example.havetime.domain.usecase.date.GetCurrentYearUseCase
import com.example.havetime.domain.usecase.date.GetInitialDateUseCase
import com.example.havetime.domain.usecase.date.GetNextYearUseCase
import com.example.havetime.domain.usecase.date.GetPreviousYearUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class YearViewModel @Inject constructor(
    private val getCurrentYearUseCase: GetCurrentYearUseCase,
    private val getNextYearUseCase: GetNextYearUseCase,
    private val getPreviousYearUseCase: GetPreviousYearUseCase,
    private val getActivitiesForMonthUseCase: GetActivitiesForMonthUseCase,
    private val getInitialDateUseCase: GetInitialDateUseCase,
    private val getCurrentDateUseCase: GetCurrentDateUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<YearState>(YearState.Loading)
    val state: StateFlow<YearState> = _state.asStateFlow()

    private val _currentYear = MutableStateFlow(getInitialDateUseCase().year)
    val currentYear: StateFlow<Int> = _currentYear.asStateFlow()

    val today: StateFlow<LocalDate> = getCurrentDateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = getInitialDateUseCase()
        )

    init {
        loadCurrentYear()
    }

    private fun loadCurrentYear() {
        viewModelScope.launch {
            try {
                val year = getCurrentYearUseCase().first()
                _currentYear.value = year
                loadYearData(year)
            } catch (e: Exception) {
                _state.value = YearState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setYear(year: Int) {
        if (_currentYear.value != year) {
            _currentYear.value = year
            loadYearData(year)
        }
    }

    fun goToNextYear() {
        val nextYear = getNextYearUseCase(_currentYear.value)
        _currentYear.value = nextYear
        loadYearData(nextYear)
    }

    fun goToPreviousYear() {
        val prevYear = getPreviousYearUseCase(_currentYear.value)
        _currentYear.value = prevYear
        loadYearData(prevYear)
    }

    private fun loadYearData(year: Int) {
        viewModelScope.launch {
            try {
                _state.value = YearState.Loading

                val monthsData = withContext(Dispatchers.Default) {
                    Month.entries.map { month ->
                        val yearMonth = YearMonth.of(year, month)
                        val daysInMonth = yearMonth.lengthOfMonth()
                        val activities = getActivitiesForMonthUseCase(yearMonth).first()

                        val daysWithActivities = activities
                            .map { it.timeInterval.startTime.toLocalDateUtc() }
                            .distinct()
                            .count()

                        YearMonthData(
                            yearMonth = yearMonth,
                            month = month,
                            daysInMonth = daysInMonth,
                            daysWithActivities = daysWithActivities,
                            completionPercentage = if (daysInMonth > 0) daysWithActivities.toFloat() / daysInMonth else 0f
                        )
                    }
                }

                _state.value = YearState.Success(year, monthsData)
            } catch (e: Exception) {
                _state.value = YearState.Error(e.message ?: "Error loading data")
            }
        }
    }
}

fun Long.toLocalDateUtc(): Int = Instant.ofEpochMilli(this)
    .atZone(ZoneOffset.UTC)
    .dayOfMonth
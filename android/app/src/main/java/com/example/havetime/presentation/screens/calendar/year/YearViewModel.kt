package com.example.havetime.presentation.screens.calendar.year

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.havetime.HaveTimeApplication
import com.example.havetime.domain.usecase.activity.GetActivitiesForMonthUseCase
import com.example.havetime.domain.usecase.date.GetCurrentYearUseCase
import com.example.havetime.domain.usecase.date.GetNextYearUseCase
import com.example.havetime.domain.usecase.date.GetPreviousYearUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.Month
import java.time.YearMonth
import java.time.ZoneOffset
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY

class YearViewModel(
    private val getCurrentYearUseCase: GetCurrentYearUseCase,
    private val getNextYearUseCase: GetNextYearUseCase,
    private val getPreviousYearUseCase: GetPreviousYearUseCase,
    private val getActivitiesForMonthUseCase: GetActivitiesForMonthUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<YearState>(YearState.Loading)
    val state: StateFlow<YearState> = _state.asStateFlow()

    private var currentYearValue: Int = 0

    init {
        loadCurrentYear()
    }

    private fun loadCurrentYear() {
        viewModelScope.launch {
            try {
                val year = getCurrentYearUseCase().first()
                currentYearValue = year
                loadYearData(year)
            } catch (e: Exception) {
                _state.value = YearState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setYear(year: Int) {
        if (currentYearValue != year) {
            currentYearValue = year
            loadYearData(year)
        }
    }

    private fun loadYearData(year: Int) {
        viewModelScope.launch {
            try {
                _state.value = YearState.Loading

                val months = Month.values().toList()
                val monthDataList = months.map { month ->
                    val yearMonth = YearMonth.of(year, month)
                    val daysInMonth = yearMonth.lengthOfMonth()

                    val activities = getActivitiesForMonthUseCase(yearMonth).first()

                    val daysWithActivities = activities
                        .map { activity ->
                            Instant.ofEpochMilli(activity.timeInterval.startTime)
                                .atZone(ZoneOffset.UTC)
                                .dayOfMonth
                        }
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

                _state.value = YearState.Success(
                    currentYear = year,
                    monthsData = monthDataList
                )
            } catch (e: Exception) {
                _state.value = YearState.Error(e.message ?: "Error loading year data")
            }
        }
    }

    fun goToNextYear() {
        val nextYear = currentYearValue + 1
        currentYearValue = nextYear
        loadYearData(nextYear)
    }

    fun goToPreviousYear() {
        val previousYear = currentYearValue - 1
        currentYearValue = previousYear
        loadYearData(previousYear)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as HaveTimeApplication)
                val activityRepo = application.todoRepository
                val dateRepo = application.dateRepository

                YearViewModel(
                    getCurrentYearUseCase = GetCurrentYearUseCase(dateRepo),
                    getNextYearUseCase = GetNextYearUseCase(dateRepo),
                    getPreviousYearUseCase = GetPreviousYearUseCase(dateRepo),
                    getActivitiesForMonthUseCase = GetActivitiesForMonthUseCase(activityRepo)
                )
            }
        }
    }
}
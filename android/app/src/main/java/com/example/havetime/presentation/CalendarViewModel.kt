package com.example.havetime.presentation

import android.icu.util.Calendar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.domain.usecase.GetIntervalsForDateUseCase
import kotlinx.coroutines.flow.*

class CalendarViewModel : ViewModel() {
    private val useCase = GetIntervalsForDateUseCase()

    private val _selectedDate = MutableStateFlow(Calendar.getInstance())
    val selectedDate = _selectedDate.asStateFlow()

    private val _allIntervals = MutableStateFlow<List<TimeInterval>>(emptyList())

    val visibleIntervals: StateFlow<List<TimeInterval>> = combine(_allIntervals, _selectedDate) { intervals, date ->
        useCase.execute(intervals, date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDate(calendar: Calendar) {
        _selectedDate.value = calendar.clone() as Calendar
    }

    fun setToday() {
        _selectedDate.value = Calendar.getInstance()
    }

    fun getIntervalsForDateSync(date: Calendar): List<TimeInterval> {
        return useCase.execute(_allIntervals.value, date)
    }

    fun getTotalBusyMinutes(date: Calendar): Int {
        return getIntervalsForDateSync(date).sumOf {
            ((it.end.timeInMillis - it.start.timeInMillis) / 60000).toInt()
        }
    }

    fun addInterval(title: String, startHour: Int, endHour: Int, color: Color) {
        val startCal = _selectedDate.value.clone() as Calendar
        startCal.set(Calendar.HOUR_OF_DAY, startHour)
        startCal.set(Calendar.MINUTE, 0)

        val endCal = _selectedDate.value.clone() as Calendar
        if (endHour == 24) {
            endCal.add(Calendar.DAY_OF_MONTH, 1)
            endCal.set(Calendar.HOUR_OF_DAY, 0)
        } else {
            endCal.set(Calendar.HOUR_OF_DAY, endHour)
        }
        endCal.set(Calendar.MINUTE, 0)

        val newInterval = TimeInterval(
            title = if (title.isEmpty()) "Активность" else title,
            start = startCal,
            end = endCal,
            color = color.toArgb()
        )
        _allIntervals.value = _allIntervals.value + newInterval
    }
}
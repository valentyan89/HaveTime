package com.example.havetime.presentation

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.domain.usecase.GetIntervalsForDateUseCase
import kotlinx.coroutines.flow.*

class CalendarViewModel : ViewModel() {
    private val useCase = GetIntervalsForDateUseCase()

    private val _selectedDate = MutableStateFlow(Calendar.getInstance())
    val selectedDate: StateFlow<Calendar> = _selectedDate.asStateFlow()

    private val _allIntervals = MutableStateFlow<List<TimeInterval>>(emptyList())

    val visibleIntervals: StateFlow<List<TimeInterval>> = combine(_allIntervals, _selectedDate) { intervals, date ->
        useCase.execute(intervals, date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateDate(calendar: Calendar) {
        _selectedDate.value = calendar
    }

    fun setToday() {
        _selectedDate.value = Calendar.getInstance()
    }

    // Тестовый метод для проверки 00:00
    fun addTestInterval() {
        val start = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 22); set(Calendar.MINUTE, 0) }
        val end = (start.clone() as Calendar).apply { add(Calendar.HOUR, 5) }
        _allIntervals.value = _allIntervals.value + TimeInterval(start = start, end = end, intensity = 85)
    }
    fun selectDate(date: Calendar) {
        _selectedDate.value = date.clone() as Calendar
    }

    // Вспомогательная функция для получения интенсивности дня (0-100)
    fun getIntensityForDate(date: Calendar): Int {
        // Пока заглушка, позже будем считать реальную занятость из репозитория
        return (0..100).random()
    }
    fun getIntervalsForDateSync(date: Calendar): List<TimeInterval> {
        return useCase.execute(_allIntervals.value, date)
    }

    // Считаем суммарную занятость в минутах для дня
    fun getTotalBusyMinutes(date: Calendar): Int {
        val dailyIntervals = getIntervalsForDateSync(date)
        return dailyIntervals.sumOf {
            ((it.end.timeInMillis - it.start.timeInMillis) / 60000).toInt()
        }
    }
}
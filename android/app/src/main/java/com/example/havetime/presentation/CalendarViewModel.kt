package com.example.havetime.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.havetime.domain.model.TimeInterval
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class CalendarViewModel : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val _allIntervals = MutableStateFlow<List<TimeInterval>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    /**
     * Реактивный поток интервалов для экрана дня.
     * Фильтрует список по выбранной дате и поисковому запросу.
     */
    val visibleIntervals: StateFlow<List<TimeInterval>> =
        combine(_allIntervals, _selectedDate, _searchQuery) { intervals, date, query ->
            intervals.filter { it.start.toLocalDate() == date }
                .filter { if (query.isEmpty()) true else it.title.contains(query, ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setToday() {
        _selectedDate.value = LocalDate.now()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Добавление нового интервала.
     */
    fun addInterval(title: String, date: LocalDate, startH: Int, endH: Int, color: Color) {
        val start = LocalDateTime.of(date, LocalTime.of(startH.coerceIn(0, 23), 0))
        val end = if (endH >= 24) {
            LocalDateTime.of(date.plusDays(1), LocalTime.MIDNIGHT)
        } else {
            LocalDateTime.of(date, LocalTime.of(endH.coerceIn(1, 24), 0))
        }

        val newInterval = TimeInterval(
            title = if (title.isEmpty()) "Активность" else title,
            start = start,
            end = end,
            color = color.toArgb()
        )
        _allIntervals.value = _allIntervals.value + newInterval
    }

    /**
     * Удаление интервала по ID.
     */
    fun removeInterval(id: String) {
        _allIntervals.value = _allIntervals.value.filter { it.id != id }
    }

    /**
     * Обновление существующего интервала.
     */
    fun updateInterval(updated: TimeInterval) {
        _allIntervals.value = _allIntervals.value.map {
            if (it.id == updated.id) updated else it
        }
    }

    /**
     * Синхронное получение интервалов для конкретной даты (используется экраном недели).
     */
    fun getIntervalsForDateSync(date: LocalDate): List<TimeInterval> {
        return _allIntervals.value.filter { it.start.toLocalDate() == date }
    }

    /**
     * Расчет суммарного времени занятости в минутах для визуализации интенсивности (HeatMap).
     */
    fun getTotalBusyMinutes(date: LocalDate): Int {
        return getIntervalsForDateSync(date).sumOf {
            ChronoUnit.MINUTES.between(it.start, it.end).toInt()
        }
    }
}
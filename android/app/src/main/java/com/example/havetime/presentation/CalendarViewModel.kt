package com.example.havetime.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.domain.model.TodoItem
import com.example.havetime.domain.usecase.AddTodoUseCase
import com.example.havetime.domain.usecase.DeleteTodoUseCase
import com.example.havetime.domain.usecase.GetIntervalsForDateUseCase
import com.example.havetime.domain.usecase.SaveIntervalUseCase
import com.example.havetime.domain.usecase.SyncWithServerUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class CalendarViewModel(
    private val addTodoUseCase: AddTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val getIntervalsForDateUseCase: GetIntervalsForDateUseCase,
    private val saveIntervalUseCase: SaveIntervalUseCase,
    private val syncWithServerUseCase: SyncWithServerUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val activities: StateFlow<List<TodoItem>> = _selectedDate
        .flatMapLatest { date ->
            getIntervalsForDateUseCase(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addActivity(todo: TodoItem) {
        viewModelScope.launch {
            addTodoUseCase(todo).collect {

            }
        }
    }

    fun deleteActivity(id: Int) {
        viewModelScope.launch {
            deleteTodoUseCase(id).collect {
            }
        }
    }

    fun syncData() {
        viewModelScope.launch {
            syncWithServerUseCase().collect()
        }
    }
}
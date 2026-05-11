package com.example.havetime.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.havetime.HaveTimeApplication
import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.domain.model.TodoItem
import com.example.havetime.domain.usecase.AddTodoUseCase
import com.example.havetime.domain.usecase.DeleteTodoUseCase
import com.example.havetime.domain.usecase.GetIntervalsForDateUseCase
import com.example.havetime.domain.usecase.GetTodosUseCase
import com.example.havetime.domain.usecase.SaveIntervalUseCase
import com.example.havetime.domain.usecase.SyncWithServerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class CalendarViewModel(
    private val addTodoUseCase: AddTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val getIntervalsForDateUseCase: GetIntervalsForDateUseCase,
    private val getTodosUseCase: GetTodosUseCase,
    private val saveIntervalUseCase: SaveIntervalUseCase,
    private val syncWithServerUseCase: SyncWithServerUseCase
) : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Задачи для выбранной даты
    val activities: StateFlow<List<TodoItem>> = _selectedDate
        .flatMapLatest { date ->
            getIntervalsForDateUseCase(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Если нужен список всех задач
    val allActivities: StateFlow<List<TodoItem>> = getTodosUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addActivity(todo: TodoItem) {
        addTodoUseCase(todo).launchIn(viewModelScope)
    }

    fun deleteActivity(id: Int) {
        deleteTodoUseCase(id).launchIn(viewModelScope)
    }

    fun saveInterval(interval: TimeInterval) {
        saveIntervalUseCase(interval).launchIn(viewModelScope)
    }

    fun syncWithServer() {
        syncWithServerUseCase().launchIn(viewModelScope)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as HaveTimeApplication)

                val repo = application.todoRepository

                CalendarViewModel(
                    addTodoUseCase = AddTodoUseCase(repo),
                    deleteTodoUseCase = DeleteTodoUseCase(repo),
                    getIntervalsForDateUseCase = GetIntervalsForDateUseCase(repo),
                    saveIntervalUseCase = SaveIntervalUseCase(repo),
                    syncWithServerUseCase = SyncWithServerUseCase(repo),
                    getTodosUseCase = GetTodosUseCase(repo)
                )
            }
        }
    }
}
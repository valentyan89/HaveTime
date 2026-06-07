package com.example.havetime.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.havetime.HaveTimeApplication
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.usecase.activity.AddTodoUseCase
import com.example.havetime.domain.usecase.activity.DeleteTodoUseCase
import com.example.havetime.domain.usecase.activity.GetIntervalsForDateUseCase
import com.example.havetime.domain.usecase.activity.GetTodosUseCase
import com.example.havetime.domain.usecase.activity.SyncWithServerUseCase
import com.example.havetime.domain.usecase.activity.UpdateActivityUseCase
import com.example.havetime.domain.usecase.activity.SearchUseCase
import com.example.havetime.domain.usecase.date.GetCurrentDateUseCase
import com.example.havetime.domain.usecase.date.GetCurrentTimeUseCase
import com.example.havetime.domain.usecase.date.GetCurrentYearUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel(
    private val addTodoUseCase: AddTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val getIntervalsForDateUseCase: GetIntervalsForDateUseCase,
    private val getTodosUseCase: GetTodosUseCase,
    private val syncWithServerUseCase: SyncWithServerUseCase,
    private val updateActivityUseCase: UpdateActivityUseCase,
    private val getCurrentTimeUseCase: GetCurrentTimeUseCase,
    private val getCurrentDateUseCase: GetCurrentDateUseCase,
    private val getCurrentYearUseCase: GetCurrentYearUseCase,
    private val searchUseCase: SearchUseCase
) : ViewModel() {
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            getCurrentDateUseCase().collect { date ->
                _selectedDate.value = date
            }
        }
    }

    val activities: StateFlow<List<Activity>> = _selectedDate
        .flatMapLatest { date ->
            when(date){
                null -> flowOf(emptyList())
                else -> getIntervalsForDateUseCase(date)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allActivities: StateFlow<List<Activity>> = getTodosUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addActivity(todo: Activity) {
        addTodoUseCase(todo).launchIn(viewModelScope)
    }

    fun deleteActivity(id: Int) {
        deleteTodoUseCase(id).launchIn(viewModelScope)
    }

    fun updateActivity(activity: Activity){
        updateActivityUseCase(activity).launchIn(viewModelScope)
    }

    fun syncWithServer() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = syncWithServerUseCase()
                result.onSuccess {
                    _errorMessage.value = null
                }
                result.onFailure {
                    _errorMessage.value = it.message ?: "ошибка синхронизации"
                }
            } catch (e: Exception){
                _errorMessage.value = e.message ?: "Неизвестная ошибка"
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as HaveTimeApplication)
                val activityRepo = application.todoRepository
                val dateRepo = application.dateRepository

                CalendarViewModel(
                    addTodoUseCase = AddTodoUseCase(activityRepo),
                    deleteTodoUseCase = DeleteTodoUseCase(activityRepo),
                    getIntervalsForDateUseCase = GetIntervalsForDateUseCase(activityRepo),
                    getTodosUseCase = GetTodosUseCase(activityRepo),
                    syncWithServerUseCase = SyncWithServerUseCase(activityRepo),
                    updateActivityUseCase = UpdateActivityUseCase(activityRepo),
                    getCurrentTimeUseCase = GetCurrentTimeUseCase(dateRepo),
                    getCurrentDateUseCase = GetCurrentDateUseCase(dateRepo),
                    getCurrentYearUseCase = GetCurrentYearUseCase(dateRepo),
                    searchUseCase = SearchUseCase(activityRepo)
                )
            }
        }
    }
}

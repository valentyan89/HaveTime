package com.example.todolist.presentation.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.domain.model.TodoItem
import com.example.havetime.presentation.CalendarViewModel
import java.time.LocalDateTime
import androidx.compose.foundation.lazy.items

@Composable
fun TestScreen(
    viewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory)
) {
    // Подписываемся на список задач (на выбранный день, по умолчанию - сегодня)
    val activities by viewModel.activities.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Генерируем тестовую задачу.
                    // ВНИМАНИЕ: Подставь сюда правильные поля из твоего класса TodoItem!
                    val testTodo = TodoItem(
                        id = 0, // 0 нужен, чтобы Room сам сгенерировал ID
                        title = "Тестовая задача ${System.currentTimeMillis()}",
                        timeInterval = TimeInterval(
                            start = LocalDateTime.now(),
                            end = LocalDateTime.now().plusHours(1),
                        ),
                        color = 100
                    )
                    viewModel.addActivity(testTodo)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(activities) { todo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Здесь тоже подставь правильные поля из TodoItem (например, todo.title)
                        Text(text = "Задача: $todo", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
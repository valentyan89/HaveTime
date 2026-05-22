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
import com.example.havetime.domain.model.Activity
import com.example.havetime.presentation.CalendarViewModel
import java.time.LocalDateTime
import androidx.compose.foundation.lazy.items

@Composable
fun TestScreen(
    viewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory)
) {
    val activities by viewModel.activities.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val testTodo = Activity(
                        id = 0,
                        title = "Тестовая задача ${System.currentTimeMillis()}",
                        timeInterval = TimeInterval(
                            startTime = LocalDateTime.now(),
                            endTime = LocalDateTime.now().plusHours(1),
                        ),
                        color = 100,
                        location = null
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
                        Text(text = "Задача: $todo", style = MaterialTheme.typography.bodyLarge)
                        Button(
                            onClick = {viewModel.deleteActivity(todo.id)}
                        ) {
                            Text("Delete activity")
                        }
                    }
                }
            }
        }
    }
}
package com.example.havetime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.example.havetime.presentation.CalendarViewModel
//import com.example.havetime.presentation.navigation.CalendarNavHost
import com.example.todolist.presentation.test.TestScreen
import kotlin.getValue

class MainActivity : ComponentActivity() {

    private val viewModel: CalendarViewModel by viewModels {
        CalendarViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
//                CalendarNavHost()
                TestScreen()
            }
        }
    }
}
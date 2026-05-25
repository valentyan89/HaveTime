package com.example.havetime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.havetime.presentation.CalendarViewModel
import com.example.havetime.presentation.navigation.CalendarNavHost

class MainActivity : ComponentActivity() {

    private val calendarViewModel: CalendarViewModel by viewModels { CalendarViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Временно отключаем проверку авторизации и сразу показываем основной экран
                CalendarNavHost(calendarViewModel)
            }
        }
    }
}
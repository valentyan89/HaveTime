package com.example.havetime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.havetime.presentation.navigation.CalendarNavHost
import com.example.havetime.ui.theme.HaveTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HaveTimeTheme(dynamicColor = false)  {
                CalendarNavHost()
            }
        }
    }
}
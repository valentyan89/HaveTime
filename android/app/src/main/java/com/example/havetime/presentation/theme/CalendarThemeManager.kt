package com.example.calendar.presentation.theme

import androidx.compose.ui.graphics.Color

object CalendarThemeManager {
    fun getColorForIntensity(intensity: Int): Color {
        return when {
            intensity > 80 -> Color.Red
            intensity > 40 -> Color.Yellow
            else -> Color.Green
        }
    }
}
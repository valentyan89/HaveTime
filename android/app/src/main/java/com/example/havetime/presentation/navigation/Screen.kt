package com.example.calendar.presentation.navigation

sealed class Screen(val route: String) {
    object Day : Screen("day_screen")
    object Week : Screen("week_screen")
    object Month : Screen("month_screen")
    object Year : Screen("year_screen")
    object Map : Screen("map_screen")
}
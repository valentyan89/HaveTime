package com.example.havetime.presentation.navigation

sealed class Screen(val route: String) {
    object Day : Screen("day_screen")
    object Month : Screen("month_screen")
    object Year : Screen("year_screen")
    object Map : Screen("map_screen")
    object Auth : Screen("auth_screen")
}
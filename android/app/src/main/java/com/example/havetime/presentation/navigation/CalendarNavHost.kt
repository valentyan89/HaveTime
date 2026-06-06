package com.example.havetime.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.havetime.presentation.screens.auth.AuthScreen
import com.example.havetime.presentation.screens.calendar.day_week.DayScreen
import com.example.havetime.presentation.screens.calendar.month.MonthScreen
//import com.example.havetime.presentation.screens.calendar.year.YearScreen
import com.example.havetime.presentation.screens.map.MapScreen



@Composable
fun CalendarNavHost() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Day.route
    ) {

        composable(Screen.Day.route) {
            DayScreen(
                navController = navController,
                onAvatarClick = { navController.navigate(Screen.Auth.route) }
            )
        }

        composable(Screen.Month.route) {
            MonthScreen(navController = navController)
        }

//        composable(Screen.Year.route) {
//            YearScreen(navController = navController)
//        }

        composable(Screen.Map.route) {
            MapScreen(navController = navController)
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = { },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
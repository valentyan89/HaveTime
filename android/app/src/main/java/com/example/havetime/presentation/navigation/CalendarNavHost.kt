package com.example.havetime.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.havetime.presentation.screens.auth.AuthScreen
import com.example.havetime.presentation.screens.calendar.day_week.DayScreen
import com.example.havetime.presentation.screens.calendar.month.MonthScreen
import com.example.havetime.presentation.screens.calendar.month.MonthViewModel
import com.example.havetime.presentation.screens.map.MapScreen
import java.time.LocalDate

@Composable
fun CalendarNavHost() {

    val navController = rememberNavController()

    // ОДИН экземпляр ViewModel для всего приложения
    val sharedViewModel: MonthViewModel = viewModel(factory = MonthViewModel.Factory)
    val selectedDate by sharedViewModel.selectedDate.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Day.route
    ) {

        composable(Screen.Day.route) {
            DayScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                initialDate = selectedDate ?: LocalDate.now(),  // ← добавить элвис-оператор
                onAvatarClick = { navController.navigate(Screen.Auth.route) }
            )
        }

        composable(Screen.Month.route) {
            MonthScreen(
                navController = navController,
                onAvatarClick = { navController.navigate(Screen.Auth.route) },
                onDayClick = { date ->
                    sharedViewModel.selectDate(date)
                    navController.navigate(Screen.Day.route)
                }
            )
        }

        composable(Screen.Map.route) {
            MapScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                initialDate = selectedDate ?: LocalDate.now(),
                onAvatarClick = { navController.navigate(Screen.Auth.route) }
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = { },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
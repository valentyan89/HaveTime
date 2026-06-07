package com.example.havetime.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val sharedViewModel: MonthViewModel = viewModel(factory = MonthViewModel.Factory)
    val selectedDate by sharedViewModel.selectedDate.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Day.route
    ) {

        composable(Screen.Day.route) {
            Box(modifier = Modifier.fillMaxSize()) {
                DayScreen(
                    navController = navController,
                    sharedViewModel = sharedViewModel,
                    initialDate = selectedDate ?: LocalDate.now(),
                    onAvatarClick = { navController.navigate(Screen.Auth.route) }
                )
            }
        }

        composable(Screen.Month.route) {
            val backStackEntry = navController.previousBackStackEntry
            val fromRoute = backStackEntry?.destination?.route

            Box(modifier = Modifier.fillMaxSize()) {
                MonthScreen(
                    navController = navController,
                    onAvatarClick = { navController.navigate(Screen.Auth.route) },
                    onDayClick = { date ->
                        sharedViewModel.selectDate(date)
                        if (fromRoute == Screen.Map.route) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(Screen.Day.route) {
                                popUpTo(Screen.Day.route) { inclusive = true }
                            }
                        }
                    }
                )
            }
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
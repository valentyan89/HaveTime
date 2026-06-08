package com.example.havetime.presentation.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.havetime.presentation.screens.auth.AuthScreen
import com.example.havetime.presentation.screens.calendar.CalendarDrawerContent
import com.example.havetime.presentation.screens.calendar.day_week.DayScreen
import com.example.havetime.presentation.screens.calendar.month.MonthScreen
import com.example.havetime.presentation.screens.calendar.month.MonthViewModel
import com.example.havetime.presentation.screens.calendar.year.YearScreen
import com.example.havetime.presentation.screens.map.MapScreen
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun CalendarNavHost() {

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val sharedViewModel: MonthViewModel = viewModel(factory = MonthViewModel.Factory)
    val selectedDate by sharedViewModel.selectedDate.collectAsState()!!

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: Screen.Day.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CalendarDrawerContent(
                navController = navController,
                currentRoute = currentRoute,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Day.route
        ) {

            composable(Screen.Day.route) {
                DayScreen(
                    navController = navController,
                    sharedViewModel = sharedViewModel,
                    initialDate = selectedDate ?: LocalDate.now(),
                    onAvatarClick = { navController.navigate(Screen.Auth.route) },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }

            composable(Screen.Month.route) {
                MonthScreen(
                    navController = navController,
                    initialDate = selectedDate ?: LocalDate.now(),
                    sharedViewModel = sharedViewModel,
                    onAvatarClick = {
                        navController.navigate(Screen.Auth.route)
                    },
                    onDayClick = { date ->
                        sharedViewModel.selectDate(date)
                        navController.navigate(Screen.Day.route)
                    },
                    onYearClick = { year ->
                        navController.navigate("${Screen.Year.route}/$year")
                    },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }

            composable(Screen.Map.route) {
                MapScreen(
                    navController = navController,
                    sharedViewModel = sharedViewModel,
                    initialDate = selectedDate ?: LocalDate.now(),
                    onAvatarClick = { navController.navigate(Screen.Auth.route) },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }

            composable(
                route = "${Screen.Year.route}/{year}",
                arguments = listOf(navArgument("year") { type = NavType.IntType })
            ) { backStackEntry ->
                val year = backStackEntry.arguments?.getInt("year") ?: LocalDate.now().year
                YearScreen(
                    navController = navController,
                    sharedViewModel = sharedViewModel,
                    initialYear = year,
                    onAvatarClick = { navController.navigate(Screen.Auth.route) },
                    onMenuClick = { scope.launch { drawerState.open() } }
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
}
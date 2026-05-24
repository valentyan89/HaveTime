package com.example.havetime.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.calendar.presentation.navigation.Screen
import com.example.havetime.domain.model.Activity
import com.example.havetime.presentation.CalendarViewModel
import com.example.havetime.presentation.common.*
import com.example.havetime.presentation.screens.day.*
import com.example.havetime.presentation.screens.week.WeekScreen
import com.example.havetime.presentation.screens.month.MonthScreen
import com.example.havetime.presentation.screens.year.YearScreen
import com.example.havetime.presentation.test.TestMapScreen
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Composable
fun CalendarNavHost(viewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory)) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedDate by viewModel.selectedDate.collectAsState()
    val activities by viewModel.activities.collectAsState()
    val allActivities by viewModel.allActivities.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingActivity by remember { mutableStateOf<Activity?>(null) }
    var newStartTime by remember { mutableStateOf(LocalTime.of(12, 0)) }
    var newEndTime by remember { mutableStateOf(LocalTime.of(13, 0)) }

    if (showAddDialog || editingActivity != null) {
        AddActivityDialog(
            editingActivity = editingActivity,
            initialDate = selectedDate,
            initialStartTime = if (editingActivity == null) newStartTime else editingActivity!!.timeInterval.start.toLocalTime(),
            initialEndTime = if (editingActivity == null) newEndTime else editingActivity!!.timeInterval.end.toLocalTime(),
            onDismiss = {
                showAddDialog = false
                editingActivity = null
            },
            onDelete = { id ->
                viewModel.deleteActivity(id)
                editingActivity = null
            },
            onConfirm = { activity ->
                if (editingActivity != null) {
                    viewModel.updateActivity(activity)
                } else {
                    viewModel.addActivity(activity)
                }
                showAddDialog = false
                editingActivity = null
            }
        )
    }

    Scaffold(
        topBar = {
            CalendarHeader(
                selectedDate = selectedDate,
                currentDestination = currentRoute,
                onDateSelected = {
                    viewModel.onDateSelected(it)
                    if (currentRoute != Screen.Day.route) navController.navigate(Screen.Day.route)
                },
                onWeekClick = { navController.navigate(Screen.Week.route) },
                onMonthClick = { navController.navigate(Screen.Month.route) },
                onYearClick = { navController.navigate(Screen.Year.route) },
                onDayClick = { navController.navigate(Screen.Day.route) }
            )
        },
        bottomBar = {
            var searchQuery by remember { mutableStateOf("") }
            BottomControlBar(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onAddClick = {
                    newStartTime = LocalTime.now().withMinute(0).plusHours(1)
                    newEndTime = newStartTime.plusHours(1)
                    showAddDialog = true
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = navController, startDestination = Screen.Day.route) {
                composable(Screen.Day.route) {
                    DayScreen(
                        intervals = activities,
                        onIntervalCreated = { start, end ->
                            newStartTime = start.toLocalTime()
                            newEndTime = end.toLocalTime()
                            showAddDialog = true
                        },
                        onIntervalClick = { editingActivity = it },
                        onIntervalUpdated = { updated ->
                            viewModel.updateActivity(updated)
                        },
                        onDateViewed = { date ->
                            viewModel.onDateSelected(date)
                        }
                    )
                }
                composable(Screen.Week.route) {
                    WeekScreen(
                        currentDate = selectedDate,
                        getIntervals = { date ->
                            allActivities.filter { it.timeInterval.start.toLocalDate() == date }
                        },
                        onDayClick = { date ->
                            viewModel.onDateSelected(date)
                            navController.navigate(Screen.Day.route)
                        }
                    )
                }
                composable(Screen.Month.route) {
                    MonthScreen(
                        currentDate = selectedDate,
                        getIntensity = { date ->
                            allActivities
                                .filter { it.timeInterval.start.toLocalDate() == date }
                                .sumOf { ChronoUnit.MINUTES.between(it.timeInterval.start, it.timeInterval.end).toInt() }
                        },
                        onDayClick = { date ->
                            viewModel.onDateSelected(date)
                            navController.navigate(Screen.Day.route)
                        }
                    )
                }
                composable(Screen.Year.route) {
                    YearScreen(
                        currentDate = selectedDate,
                        onMonthClick = { date ->
                            viewModel.onDateSelected(date)
                            navController.navigate(Screen.Month.route)
                        }
                    )
                }
                composable(Screen.Map.route) {
                    TestMapScreen(viewModel = viewModel)
                }
            }
        }
    }
}
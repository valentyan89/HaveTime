package com.example.havetime.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.calendar.presentation.navigation.Screen
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.Location
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
    val allActivities by viewModel.allActivities.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()

    val dayActivities = allActivities

    var showAddDialog by remember { mutableStateOf(false) }
    var editingActivity by remember { mutableStateOf<Activity?>(null) }
    
    val defaultStartTime = remember(currentTime) { currentTime.toLocalTime() }
    var newStartTime by remember { mutableStateOf(defaultStartTime) }
    var newEndTime by remember { mutableStateOf(defaultStartTime.plusHours(1)) }
    
    var pendingLocation by remember { mutableStateOf<Location?>(null) }

    if (showAddDialog || editingActivity != null) {
        AddActivityDialog(
            editingActivity = editingActivity,
            allActivities = allActivities,
            initialDate = selectedDate,
            initialStartTime = if (editingActivity == null) newStartTime else editingActivity!!.timeInterval.start.toLocalTime(),
            initialEndTime = if (editingActivity == null) newEndTime else editingActivity!!.timeInterval.end.toLocalTime(),
            initialLocation = pendingLocation ?: editingActivity?.location,
            onDismiss = {
                showAddDialog = false
                editingActivity = null
                pendingLocation = null
            },
            onDelete = { id ->
                viewModel.deleteActivity(id)
                editingActivity = null
                pendingLocation = null
            },
            onConfirm = { activity ->
                if (editingActivity != null) {
                    viewModel.updateActivity(activity)
                } else {
                    viewModel.addActivity(activity)
                }
                showAddDialog = false
                editingActivity = null
                pendingLocation = null
            }
        )
    }

    Scaffold(
        topBar = {
            CalendarHeader(
                selectedDate = selectedDate,
                todayDate = currentTime.toLocalDate(),
                currentDestination = currentRoute,
                onDateSelected = {
                    viewModel.onDateSelected(it)
                    if (currentRoute != Screen.Day.route && currentRoute != Screen.Map.route) {
                        navController.navigate(Screen.Day.route)
                    }
                },
                onTodayClick = {
                    viewModel.onDateSelected(currentTime.toLocalDate())
                    if (currentRoute != Screen.Day.route && currentRoute != Screen.Map.route) {
                        navController.navigate(Screen.Day.route)
                    }
                },
                onMonthClick = { 
                    navController.navigate(Screen.Month.route) 
                }
            )
        },
        bottomBar = {
            var searchQuery by remember { mutableStateOf("") }
            val isMapScreen = currentRoute == Screen.Map.route
            
            BottomControlBar(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onAddClick = {
                    newStartTime = currentTime.toLocalTime()
                    newEndTime = newStartTime.plusHours(1)
                    showAddDialog = true
                },
                onMapClick = {
                    if (isMapScreen) {
                        navController.navigate(Screen.Day.route)
                    } else {
                        navController.navigate(Screen.Map.route)
                    }
                },
                onMapStyleClick = {
                    // Пока заглушка
                },
                onProfileClick = {
                    viewModel.logout()
                },
                isMapScreen = isMapScreen
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = navController, startDestination = Screen.Day.route) {
                composable(Screen.Day.route) {
                    DayScreen(
                        selectedDate = selectedDate,
                        currentTime = currentTime,
                        intervals = dayActivities,
                        onIntervalCreated = { start, end ->
                            newStartTime = start.toLocalTime()
                            newEndTime = end.toLocalTime()
                            showAddDialog = true
                        },
                        onIntervalClick = { editingActivity = it },
                        onIntervalUpdated = { updated ->
                            viewModel.updateActivity(updated)
                        },
                        onDateChanged = { date ->
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
                            navController.popBackStack()
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
                    TestMapScreen(
                        viewModel = viewModel,
                        onCreateActivityAtLocation = { location ->
                            pendingLocation = location
                            newStartTime = currentTime.toLocalTime()
                            newEndTime = newStartTime.plusHours(1)
                            showAddDialog = true
                        },
                        onActivityClick = { activity ->
                            editingActivity = activity
                        }
                    )
                }
            }
        }
    }
}

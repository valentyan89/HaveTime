package com.example.havetime.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.presentation.CalendarViewModel
import com.example.havetime.presentation.common.*
import com.example.havetime.presentation.screens.day.*
import com.example.havetime.presentation.screens.week.WeekScreen
import com.example.havetime.presentation.screens.month.MonthScreen
import com.example.havetime.presentation.screens.year.YearScreen
import java.time.LocalTime

@Composable
fun CalendarNavHost(viewModel: CalendarViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedDate by viewModel.selectedDate.collectAsState()
    val intervals by viewModel.visibleIntervals.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingInterval by remember { mutableStateOf<TimeInterval?>(null) }
    var newStartTime by remember { mutableStateOf(LocalTime.of(12, 0)) }
    var newEndTime by remember { mutableStateOf(LocalTime.of(13, 0)) }

    if (showAddDialog || editingInterval != null) {
        AddActivityDialog(
            editingInterval = editingInterval,
            initialDate = selectedDate,
            initialStartTime = if (editingInterval == null) newStartTime else editingInterval!!.start.toLocalTime(),
            initialEndTime = if (editingInterval == null) newEndTime else editingInterval!!.end.toLocalTime(),
            onDismiss = {
                showAddDialog = false
                editingInterval = null
            },
            onDelete = { id ->
                viewModel.removeInterval(id)
                editingInterval = null
            },
            onConfirm = { interval ->
                if (editingInterval != null) {
                    viewModel.updateInterval(interval)
                } else {
                    viewModel.addInterval(
                        title = interval.title,
                        date = selectedDate,
                        startH = interval.start.hour,
                        startM = interval.start.minute,
                        endH = interval.end.hour,
                        endM = interval.end.minute,
                        color = androidx.compose.ui.graphics.Color(interval.color)
                    )
                }
                showAddDialog = false
                editingInterval = null
            }
        )
    }

    Scaffold(
        topBar = {
            CalendarHeader(
                selectedDate = selectedDate,
                currentDestination = currentRoute,
                onDateSelected = {
                    viewModel.selectDate(it)
                    if (currentRoute != "day") navController.navigate("day")
                },
                onWeekClick = { navController.navigate("week") },
                onMonthClick = { navController.navigate("month") },
                onYearClick = { navController.navigate("year") },
                onDayClick = { navController.navigate("day") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = navController, startDestination = "day") {
                composable("day") {
                    DayScreen(
                        intervals = intervals,
                        onIntervalCreated = { start, end ->
                            newStartTime = start
                            newEndTime = end
                            showAddDialog = true
                        },
                        onIntervalClick = { editingInterval = it },
                        onIntervalUpdated = { updatedInterval ->
                            viewModel.updateInterval(updatedInterval)
                        }
                    )
                }
                composable("week") {
                    WeekScreen(selectedDate, { d -> viewModel.getIntervalsForDateSync(d) }) { d ->
                        viewModel.selectDate(d)
                        navController.navigate("day")
                    }
                }
                composable("month") {
                    MonthScreen(selectedDate, { d -> viewModel.getTotalBusyMinutes(d) }) { d ->
                        viewModel.selectDate(d)
                        navController.navigate("day")
                    }
                }
                composable("year") {
                    YearScreen(selectedDate) { d ->
                        viewModel.selectDate(d)
                        navController.navigate("month")
                    }
                }
            }
        }
    }
}
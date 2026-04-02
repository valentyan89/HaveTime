package com.example.havetime.presentation.navigation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.havetime.presentation.CalendarViewModel
import com.example.havetime.presentation.common.CalendarHeader
import com.example.havetime.presentation.screens.day.DayScreen
import com.example.havetime.presentation.screens.week.WeekScreen
import com.example.havetime.presentation.screens.month.MonthScreen
import com.example.havetime.presentation.screens.year.YearScreen

@Composable
fun CalendarNavHost(viewModel: CalendarViewModel = viewModel()) {
    val navController = rememberNavController()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val intervals by viewModel.visibleIntervals.collectAsState()

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(Color(0xFF664FA3))
        )
        CalendarHeader(
            selectedDate = selectedDate,
            onDayClick = { viewModel.setToday(); navController.navigate("day") },
            onWeekClick = { navController.navigate("week") },
            onMonthClick = { navController.navigate("month") },
            onYearClick = { navController.navigate("year") }
        )
        NavHost(navController = navController, startDestination = "day") {
            composable("day") {
                DayScreen(intervals) { title, s, e, c -> viewModel.addInterval(title, s, e, c) }
            }
            composable("week") {
                WeekScreen(selectedDate, { d -> viewModel.getIntervalsForDateSync(d) }) { d ->
                    viewModel.selectDate(d); navController.navigate("day")
                }
            }

            composable("month") {
                MonthScreen(
                    currentDate = selectedDate,
                    getIntensity = { date -> viewModel.getTotalBusyMinutes(date) },
                    onDayClick = { date ->
                        viewModel.selectDate(date)
                        navController.navigate("day")
                    }
                )
            }

            composable("year") {
                YearScreen(
                    currentDate = selectedDate,
                    onMonthClick = { date ->
                        viewModel.selectDate(date)
                        navController.navigate("month")
                    }
                )
            }
        }
    }
}
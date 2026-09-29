package com.example.havetime.presentation.screens.calendar.day_week

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.havetime.presentation.screens.CalendarMode
import com.example.havetime.presentation.screens.calendar.month.MonthViewModel

private const val INFINITE_PAGER_ITEMS = 10_000
private const val PAGER_START_INDEX = INFINITE_PAGER_ITEMS / 2

@Composable
fun DayScreen(
    navController: NavController,
    sharedViewModel: MonthViewModel,
    viewModel: WeekDayViewModel = hiltViewModel(),
    onAvatarClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
) {

    val currentDate by viewModel.currentDate.collectAsState()
    val currentTimeState by viewModel.currentTime.collectAsState()
    val events by viewModel.activityForDate.collectAsState()
    val calendarMode by viewModel.calendarMode.collectAsState()
    val today by viewModel.today.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val sharedSelectedDate by sharedViewModel.selectedDate.collectAsState()

    LaunchedEffect(sharedSelectedDate) {
        viewModel.selectDate(sharedSelectedDate)
    }

    LaunchedEffect(Unit) {
        if (calendarMode != CalendarMode.WEEK_DAY) {
            viewModel.setCalendarMode(CalendarMode.WEEK_DAY)
        }
    }

    DayScreenContent(
        date = currentDate,
        today = today,
        currentTime = currentTimeState,
        events = events,
        searchQuery = searchQuery,
        searchResults = searchResults,
        navController = navController,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onGo2Today = viewModel::go2Today,
        onSelectDate = viewModel::selectDate,
        onUpdateActivity = viewModel::updateActivity,
        onAddActivity = viewModel::addActivity,
        onDeleteActivity = viewModel::deleteActivity,
        onMenuClick = onMenuClick,
        onAvatarClick = onAvatarClick
    )
}
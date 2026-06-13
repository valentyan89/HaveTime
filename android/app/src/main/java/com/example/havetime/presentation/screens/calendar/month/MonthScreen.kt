package com.example.havetime.presentation.screens.calendar.month

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthScreen(
    navController: NavController,
    initialDate: LocalDate = LocalDate.now(),
    monthViewModel: MonthViewModel,
    onAvatarClick: () -> Unit = {},
    onDayClick: (LocalDate) -> Unit = {},
    onYearClick: (Int) -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    LaunchedEffect(initialDate) {
        if (monthViewModel.currentMonth.value == null) {
            monthViewModel.setMonth(YearMonth.from(initialDate))
        }
    }
    val currentMonth by monthViewModel.currentMonth.collectAsState()
    val intensityMap by monthViewModel.intensityMap.collectAsState()

    if (currentMonth == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val month = currentMonth!!
    val startMonth = remember(month) { YearMonth.of(month.year - 10, 1) }
    val endMonth = remember(month) { YearMonth.of(month.year + 10, 12) }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = month,
        firstDayOfWeek = daysOfWeek.first()
    )
    val visibleMonth = state.firstVisibleMonth.yearMonth
    LaunchedEffect(visibleMonth) {
        monthViewModel.setMonth(visibleMonth)
    }

    val today = LocalDate.now()
    val currentLocale = remember { Locale.getDefault() }

    Scaffold(
        topBar = { Spacer(modifier = Modifier.statusBarsPadding()) },
        bottomBar = {
            MonthBottomNavigationBar(
                navController = navController,
                onCalendarClick = {}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    MonthTopBar(
                        visibleMonth = visibleMonth,
                        today = today,
                        currentLocale = currentLocale,
                        onMenuClick = onMenuClick,
                        onGo2Today = monthViewModel::go2Today,
                        onYearClick = onYearClick,
                        onAvatarClick = onAvatarClick
                    )

                    DaysOfWeekHeader(
                        daysOfWeek = daysOfWeek,
                        currentLocale = currentLocale
                    )
                }
            }

            VerticalCalendar(
                state = state,
                modifier = Modifier.padding(horizontal = 8.dp),
                dayContent = { day ->
                    MonthDayCell(
                        day = day,
                        today = today,
                        intensityMap = intensityMap,
                        onDayClick = onDayClick
                    )
                },
                monthHeader = { monthData ->
                    MonthSectionHeader(
                        monthData = monthData,
                        currentLocale = currentLocale
                    )
                }
            )
        }
    }
}
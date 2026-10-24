package com.example.havetime.presentation.screens.calendar.month

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.havetime.presentation.common.navigation.bottom_bar.LocalSharedBottomBar
import com.example.havetime.presentation.common.utils.navigationBottomPadding
import com.example.havetime.presentation.screens.calendar.month.components.MonthDayCell
import com.example.havetime.presentation.screens.calendar.month.components.MonthScreenHeader
import com.example.havetime.presentation.screens.calendar.month.components.MonthSectionHeader
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthScreen(
    monthViewModel: MonthViewModel,
    onAvatarClick: () -> Unit = {},
    onDayClick: (LocalDate) -> Unit = {},
    onYearClick: (Int) -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    val currentMonth by monthViewModel.currentMonth.collectAsState()
    val intensityMap by monthViewModel.intensityMap.collectAsState()

    val month = currentMonth
    val startMonth = remember(month) { YearMonth.of(month.year - 10, 1) }
    val endMonth = remember(month) { YearMonth.of(month.year + 10, 12) }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }

    val hazeState = LocalSharedBottomBar.current?.hazeState ?: remember { HazeState() }

    val state =
        rememberCalendarState(
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
    val currentLocale = LocalConfiguration.current.locales[0]

    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        topBar = {
            MonthScreenHeader(
                modifier = Modifier.statusBarsPadding(),
                visibleMonth = visibleMonth,
                today = today,
                currentLocale = currentLocale,
                daysOfWeek = daysOfWeek,
                isSearchActive = false,
                onMenuClick = onMenuClick,
                onGo2Today = monthViewModel::go2Today,
                onYearClick = onYearClick,
                onAvatarClick = onAvatarClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .haze(
                    hazeState,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    tint = Color.Black.copy(alpha = .2f),
                    blurRadius = 30.dp
                )
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding()
                )
                .navigationBottomPadding()
        ) {
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

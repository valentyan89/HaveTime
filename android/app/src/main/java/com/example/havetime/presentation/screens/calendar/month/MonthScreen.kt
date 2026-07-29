package com.example.havetime.presentation.screens.calendar.month

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.havetime.presentation.common.bottom_bar_tab.GlassyBottomBar
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.daysOfWeek
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthScreen(
    navController: NavController,
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

    val hazeState = remember { HazeState() }

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

    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        topBar = { Spacer(modifier = Modifier.statusBarsPadding()) },
        bottomBar = {
            GlassyBottomBar(
                navController = navController,
                hazeState = hazeState
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .haze(
                    hazeState,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    tint = Color.Black.copy(alpha = .2f),
                    blurRadius = 30.dp,
                )
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = navigationBarsPadding
                )
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
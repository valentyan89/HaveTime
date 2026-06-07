package com.example.havetime.presentation.screens.calendar.month

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.example.havetime.R
import com.example.havetime.presentation.navigation.Screen
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthScreen(
    navController: NavController,
    initialDate: LocalDate = LocalDate.now(),
    viewModel: MonthViewModel = viewModel(factory = MonthViewModel.Factory),
    onAvatarClick: () -> Unit = {},
    onDayClick: (LocalDate) -> Unit = {},
    onYearClick: (Int) -> Unit = {}
) {
    LaunchedEffect(initialDate) {
        viewModel.setMonth(YearMonth.from(initialDate))
    }
    val currentMonth by viewModel.currentMonth.collectAsState()
    val intensityMap by viewModel.intensityMap.collectAsState()
    val intensityHigh = colorResource(R.color.intensity_high)
    val intensityMedium = colorResource(R.color.intensity_medium)
    val intensityLow = colorResource(R.color.intensity_low)

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
        viewModel.setMonth(visibleMonth)
    }

    val today = LocalDate.now()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu))
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                    }
                    IconButton(onClick = onAvatarClick) {
                        Icon(Icons.Default.AccountCircle, contentDescription = stringResource(R.string.profile))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.calendar)) },
                        label = { Text(stringResource(R.string.calendar)) },
                        selected = true,
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Map, contentDescription = stringResource(R.string.map)) },
                        label = { Text(stringResource(R.string.map)) },
                        selected = false,
                        onClick = { navController.navigate(Screen.Map.route) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { viewModel.go2Today() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = today.dayOfMonth.toString(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Text(
                    text = visibleMonth.year.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onYearClick(visibleMonth.year) }
                )
            }

            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                daysOfWeek.forEach { dayOfWeek ->
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            VerticalCalendar(
                state = state,
                dayContent = { day ->
                    val isToday = day.date == today
                    val isCurrentMonth = day.position == DayPosition.MonthDate

                    val intensity = if (isCurrentMonth) {
                        intensityMap[day.date] ?: 0
                    } else 0

                    val backgroundColor = when {
                        !isCurrentMonth -> Color.Transparent
                        intensity >= 8 -> intensityHigh.copy(alpha = 0.4f)
                        intensity >= 4 -> intensityMedium.copy(alpha = 0.3f)
                        intensity > 0 -> intensityLow.copy(alpha = 0.2f)
                        else -> Color.Transparent
                    }

                    val textColor = when {
                        !isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        isToday -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (isToday && isCurrentMonth) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    backgroundColor
                                }
                            )
                            .clickable(enabled = true) {
                                onDayClick(day.date)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.date.dayOfMonth.toString(),
                            fontSize = 15.sp,
                            fontWeight = if (isToday && isCurrentMonth) FontWeight.Bold else FontWeight.Normal,
                            color = textColor
                        )
                    }
                },
                monthHeader = { monthData ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 12.dp, start = 8.dp)
                    ) {
                        Text(
                            text = monthData.yearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru"))
                                .replaceFirstChar { it.uppercase() } + " ${monthData.yearMonth.year}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        HorizontalDivider(modifier = Modifier.padding(top = 4.dp, end = 16.dp))
                    }
                }
            )
        }
    }
}

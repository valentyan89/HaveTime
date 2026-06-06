package com.example.havetime.presentation.screens.calendar.month


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import com.example.havetime.presentation.navigation.Screen





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthScreen(
    navController: NavController,
    viewModel: MonthViewModel = viewModel(factory = MonthViewModel.Factory),
    onDayClick: (LocalDate) -> Unit = {},
    onYearClick: (Int) -> Unit = {},
    onAvatarClick: () -> Unit = {}
) {
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
    val startMonth = remember { currentMonth!!.minusYears(50) }
    val endMonth = remember { currentMonth!!.plusYears(50) }
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
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more))
                    }
                    IconButton(onClick = onAvatarClick) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.profile)
                        )
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
            // Заголовок с годом (кликабельный)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
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
                        text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            VerticalCalendar(
                state = state,
                dayContent = { day ->
                    val isToday = day.date == LocalDate.now()
                    val intensity = if (day.position == DayPosition.MonthDate) {
                        intensityMap[day.date] ?: 0
                    } else 0

                    val backgroundColor = when {
                        intensity >= 8 -> intensityHigh.copy(alpha = 0.4f)
                        intensity >= 4 -> intensityMedium.copy(alpha = 0.3f)
                        intensity > 0 -> intensityLow.copy(alpha = 0.2f)
                        else -> Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else backgroundColor)
                            .clickable(enabled = day.position == DayPosition.MonthDate) {
                                onDayClick(day.date)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.date.dayOfMonth.toString(),
                            fontSize = 15.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                day.position != DayPosition.MonthDate -> MaterialTheme.colorScheme.onSurfaceVariant
                                isToday -> MaterialTheme.colorScheme.onPrimary
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                },
                monthHeader = { monthData ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp, horizontal = 8.dp),
                        textAlign = TextAlign.Start,
                        text = monthData.yearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
                            .replaceFirstChar { it.uppercase() } + " ${monthData.yearMonth.year}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}
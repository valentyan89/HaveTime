package com.example.havetime.presentation.screens.calendar.year

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.havetime.R
import com.example.havetime.presentation.common.bottom_bar_tab.GlassyBottomBar
import com.example.havetime.presentation.navigation.Screen
import com.example.havetime.presentation.screens.calendar.month.MonthViewModel
import dev.chrisbanes.haze.HazeState
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearScreen(
    navController: NavController,
    sharedViewModel: MonthViewModel,
    onAvatarClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    viewModel: YearViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val today by viewModel.today.collectAsState()

    val currentLocale = remember { Locale.getDefault() }

    val hazeState = remember { HazeState() }
    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        topBar = {
            Spacer(modifier = Modifier.statusBarsPadding())
        },
        bottomBar = {
            GlassyBottomBar(
                navController = navController,
                hazeState = hazeState
            )
        }
    ) { paddingValues ->
        when (val yearState = state) {
            is YearState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is YearState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = yearState.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is YearState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.align(Alignment.CenterStart),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = onMenuClick) {
                                        Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu))
                                    }
                                    
                                    Spacer(modifier = Modifier.width(4.dp))

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .clickable { 
                                                viewModel.setYear(today.year)
                                                sharedViewModel.go2Today() 
                                            },
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
                                }

                                Row(
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { }) {
                                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                                    }
                                    IconButton(onClick = onAvatarClick) {
                                        Icon(Icons.Default.AccountCircle, contentDescription = stringResource(R.string.profile))
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { viewModel.goToPreviousYear() }) {
                                    Icon(Icons.Default.ChevronLeft, contentDescription = null)
                                }
                                Text(
                                    text = yearState.currentYear.toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                IconButton(onClick = { viewModel.goToNextYear() }) {
                                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                                }
                            }
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(yearState.monthsData) { monthData ->
                            val isCurrentMonth = monthData.month == today.month && yearState.currentYear == today.year

                            MonthItem(
                                monthData = monthData,
                                isCurrentMonth = isCurrentMonth,
                                onClick = {
                                    sharedViewModel.setMonth(monthData.yearMonth)
                                    navController.navigate(Screen.Month.route)
                                },
                                locale = currentLocale
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthItem(
    monthData: YearMonthData,
    isCurrentMonth: Boolean,
    onClick: () -> Unit,
    locale: Locale
) {
    val monthName = monthData.month.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = monthName,
            style = MaterialTheme.typography.labelLarge,
            color = if (isCurrentMonth) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    if (monthData.daysWithActivities > 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .border(
                    width = if (isCurrentMonth) 2.dp else 0.dp,
                    color = if (isCurrentMonth) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (monthData.daysWithActivities > 0) {
                Text(
                    text = monthData.daysWithActivities.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

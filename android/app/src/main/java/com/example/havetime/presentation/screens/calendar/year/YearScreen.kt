package com.example.havetime.presentation.screens.calendar.year

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.havetime.R
import com.example.havetime.presentation.navigation.Screen
import com.example.havetime.presentation.screens.calendar.month.MonthViewModel
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import androidx.compose.foundation.border

private val CARD_SHAPE = RoundedCornerShape(12.dp)
private val CARD_ELEVATION = 1.dp
private val CIRCLE_SIZE = 80.dp
private val CIRCLE_STROKE = 5.dp
private val CURRENT_MONTH_BORDER = 1.5.dp
private val NORMAL_MONTH_ALPHA = 0.5f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearScreen(
    navController: NavController,
    sharedViewModel: MonthViewModel,
    initialYear: Int,
    onAvatarClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    viewModel: YearViewModel = viewModel(factory = YearViewModel.Factory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentYear = YearMonth.now().year
    val currentMonth = YearMonth.now().month

    LaunchedEffect(initialYear) {
        viewModel.setYear(initialYear)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
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
    ) { paddingValues ->
        when (state) {
            is YearState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is YearState.Success -> {
                val successState = state as YearState.Success

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.goToPreviousYear() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Previous Year")
                        }

                        Spacer(modifier = Modifier.width(32.dp))

                        Text(
                            text = successState.currentYear.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(32.dp))

                        IconButton(onClick = { viewModel.goToNextYear() }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Next Year")
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(successState.monthsData) { monthData ->
                            val isCurrentMonth = successState.currentYear == currentYear &&
                                    monthData.month == currentMonth

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clickable {
                                        sharedViewModel.selectMonth(monthData.yearMonth)
                                        navController.navigate(Screen.Month.route) {
                                            launchSingleTop = true
                                        }
                                    },
                                shape = CARD_SHAPE,
                                elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .then(
                                            if (isCurrentMonth) {
                                                Modifier.border(
                                                    width = CURRENT_MONTH_BORDER,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = CARD_SHAPE
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Text(
                                            text = monthData.month.getDisplayName(TextStyle.SHORT, Locale("ru")),
                                            fontSize = 16.sp,
                                            fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isCurrentMonth) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            }
                                        )

                                        Box(
                                            modifier = Modifier.size(CIRCLE_SIZE),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                progress = monthData.completionPercentage,
                                                modifier = Modifier.fillMaxSize(),
                                                strokeWidth = CIRCLE_STROKE,
                                                color = if (isCurrentMonth) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.primary.copy(alpha = NORMAL_MONTH_ALPHA)
                                                },
                                                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                            )

                                            Text(
                                                text = "${monthData.daysWithActivities}/${monthData.daysInMonth}",
                                                fontSize = 12.sp,
                                                fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isCurrentMonth) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is YearState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ошибка: ${(state as YearState.Error).message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
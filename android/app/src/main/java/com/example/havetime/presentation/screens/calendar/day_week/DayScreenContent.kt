package com.example.havetime.presentation.screens.calendar.day_week

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.havetime.R
import com.example.havetime.domain.model.Activity
import com.example.havetime.presentation.common.AddActivityDialog
import com.example.havetime.presentation.common.GlassyFloatingActionButton
import com.example.havetime.presentation.common.bottom_bar_tab.GlassyBottomBar
import com.example.havetime.presentation.navigation.Screen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

private const val INFINITE_PAGER_ITEMS = 10_000
private const val PAGER_START_INDEX = INFINITE_PAGER_ITEMS / 2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayScreenContent(
    date: LocalDate,
    today: LocalDate,
    currentTime: LocalDateTime,
    events: List<Activity>,
    searchQuery: String,
    searchResults: List<Activity>,
    navController: NavController,
    onSearchQueryChanged: (String) -> Unit,
    onGo2Today: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onUpdateActivity: (Activity) -> Unit,
    onAddActivity: (Activity) -> Unit,
    onDeleteActivity: (Int) -> Unit,
    onMenuClick: () -> Unit,
    onAvatarClick: () -> Unit,
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var showEventDialog by remember { mutableStateOf(false) }
    var editingActivity by remember { mutableStateOf<Activity?>(null) }
    var draftStartTime by remember { mutableStateOf<Long?>(null) }
    var draftEndTime by remember { mutableStateOf<Long?>(null) }

    val currentLocale = remember { Locale.getDefault() }

    val hazeState = remember { HazeState() }
    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        topBar = { Spacer(modifier = Modifier.statusBarsPadding()) },
        floatingActionButton = {
            GlassyFloatingActionButton(
                onClick = { showEventDialog = true },
                hazeState = hazeState
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_event), tint = MaterialTheme.colorScheme.onSurface)
            }
        },
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
                .padding(top = paddingValues.calculateTopPadding(), bottom = navigationBarsPadding)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    DayTopBar(
                        date = date,
                        today = today,
                        searchQuery = searchQuery,
                        isSearchActive = isSearchActive,
                        currentLocale = currentLocale,
                        onSearchActiveChanged = { isSearchActive = it },
                        onSearchQueryChanged = onSearchQueryChanged,
                        onGo2Today = onGo2Today,
                        onMenuClick = onMenuClick,
                        onAvatarClick = onAvatarClick,
                        onTitleClick = { navController.navigate(Screen.Month.route) },
                    )

                    if (!isSearchActive) {
                        WeekCalendarPager(
                            date = date,
                            today = today,
                            currentLocale = currentLocale,
                            onSelectDate = onSelectDate
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (isSearchActive && searchQuery.isNotEmpty()) {
                    SearchResultsList(
                        searchResults = searchResults,
                        currentLocale = currentLocale,
                        onActivityClick = { activity ->
                            val activityDate = Instant.ofEpochMilli(activity.timeInterval.startTime)
                                .atZone(ZoneId.systemDefault()).toLocalDate()
                            onSelectDate(activityDate)
                            isSearchActive = false
                        }
                    )
                }

                DayTimeline(
                    events = events,
                    currentDateTime = currentTime,
                    selectedDate = date,
                    onEventClick = { activity ->
                        editingActivity = activity
                        showEventDialog = true
                    },
                    onAddActivity = { startTime, endTime ->
                        draftStartTime = startTime
                        draftEndTime = endTime
                        editingActivity = null
                        showEventDialog = true
                    },
                    onUpdateActivity = onUpdateActivity,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

    if (showEventDialog) {
        val startInstant = editingActivity?.timeInterval?.startTime ?: draftStartTime
        val endInstant = editingActivity?.timeInterval?.endTime ?: draftEndTime

        AddActivityDialog(
            editingActivity = editingActivity,
            initialDate = startInstant?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() } ?: date,
            initialStartTime = startInstant?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalTime() } ?: currentTime.toLocalTime(),
            initialEndTime = endInstant?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalTime() } ?: currentTime.toLocalTime().plusHours(1),
            onDismiss = {
                showEventDialog = false
                editingActivity = null
                draftStartTime = null
                draftEndTime = null
            },
            onConfirm = { activity ->
                if (editingActivity != null) onUpdateActivity(activity) else onAddActivity(activity)
                showEventDialog = false
                editingActivity = null
                draftStartTime = null
                draftEndTime = null
            },
            onDelete = { activityId ->
                onDeleteActivity(activityId)
                showEventDialog = false
                editingActivity = null
                draftStartTime = null
                draftEndTime = null
            }
        )
    }
}
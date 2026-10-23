package com.example.havetime.presentation.screens.calendar.day_week.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.havetime.presentation.common.components.HaveTopAppBar
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@Composable
internal fun DayScreenHeader(
    modifier: Modifier = Modifier,
    date: LocalDate,
    today: LocalDate,
    searchQuery: String,
    isSearchActive: Boolean,
    currentLocale: Locale,
    onSearchActiveChanged: (Boolean) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onGo2Today: () -> Unit,
    onMenuClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onTitleClick: () -> Unit,
    onSelectDate: (LocalDate) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            HaveTopAppBar(
                visibleMonth = YearMonth.from(date),
                today = today,
                currentLocale = currentLocale,
                onMenuClick = onMenuClick,
                onGoToToday = onGo2Today,
                onTitleClick = onTitleClick,
                onAvatarClick = onAvatarClick,
                isSearchActive = isSearchActive,
                searchQuery = searchQuery,
                onSearchActiveChanged = onSearchActiveChanged,
                onSearchQueryChanged = onSearchQueryChanged
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
}

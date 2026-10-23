package com.example.havetime.presentation.screens.calendar.day_week.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.util.Locale

@Composable
internal fun DayScreenHeader(
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
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            DayTopBar(
                date = date,
                today = today,
                searchQuery = searchQuery,
                isSearchActive = isSearchActive,
                currentLocale = currentLocale,
                onSearchActiveChanged = onSearchActiveChanged,
                onSearchQueryChanged = onSearchQueryChanged,
                onGo2Today = onGo2Today,
                onMenuClick = onMenuClick,
                onAvatarClick = onAvatarClick,
                onTitleClick = onTitleClick
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

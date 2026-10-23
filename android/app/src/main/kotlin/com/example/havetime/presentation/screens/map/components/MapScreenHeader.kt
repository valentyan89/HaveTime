
package com.example.havetime.presentation.screens.map.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.havetime.presentation.common.components.HaveTopAppBar
import com.example.havetime.presentation.screens.calendar.day_week.components.WeekCalendarPager
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@Composable
internal fun MapScreenHeader(
    date: LocalDate,
    today: LocalDate,
    currentLocale: Locale,
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchActiveChanged: (Boolean) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onGo2Today: () -> Unit,
    onMonthClick: () -> Unit,
    onMenuClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            HaveTopAppBar(
                modifier = Modifier,
                visibleMonth = YearMonth.from(date),
                today = today,
                currentLocale = currentLocale,
                isSearchActive = isSearchActive,
                searchQuery = searchQuery,
                onSearchActiveChanged = onSearchActiveChanged,
                onSearchQueryChanged = onSearchQueryChanged,
                onGoToToday = onGo2Today,
                onTitleClick = onMonthClick,
                onMenuClick = onMenuClick,
                onAvatarClick = onAvatarClick
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

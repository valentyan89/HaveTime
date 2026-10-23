package com.example.havetime.presentation.screens.calendar.month.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@Composable
internal fun MonthScreenHeader(
    visibleMonth: YearMonth,
    today: LocalDate,
    currentLocale: Locale,
    daysOfWeek: List<DayOfWeek>,
    onMenuClick: () -> Unit,
    onGo2Today: () -> Unit,
    onYearClick: (Int) -> Unit,
    onAvatarClick: () -> Unit
) {
    Card(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            MonthTopBar(
                visibleMonth = visibleMonth,
                today = today,
                currentLocale = currentLocale,
                onMenuClick = onMenuClick,
                onGo2Today = onGo2Today,
                onYearClick = onYearClick,
                onAvatarClick = onAvatarClick
            )

            DaysOfWeekHeader(
                daysOfWeek = daysOfWeek,
                currentLocale = currentLocale
            )
        }
    }
}

package com.example.havetime.presentation.screens.calendar.day_week

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.havetime.domain.model.Activity
import com.example.havetime.presentation.common.utils.HaveText
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

private const val INFINITE_PAGER_ITEMS = 10_000
private const val PAGER_START_INDEX = INFINITE_PAGER_ITEMS / 2

@Composable
fun SearchResultsList(
    searchResults: List<Activity>,
    currentLocale: Locale,
    onActivityClick: (Activity) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .zIndex(100f)
    ) {
        items(searchResults) { activity ->
            ListItem(
                headlineContent = { HaveText(activity.title) },
                supportingContent = {
                    val start = Instant.ofEpochMilli(activity.timeInterval.startTime).atZone(ZoneId.systemDefault())
                    val formattedMonth = start.month.getDisplayName(TextStyle.SHORT, currentLocale)
                    HaveText(String.format(currentLocale, "%d %s %d:%02d", start.dayOfMonth, formattedMonth, start.hour, start.minute))
                },
                leadingContent = {
                    Box(modifier = Modifier.size(12.dp).background(Color(activity.color), CircleShape))
                },
                modifier = Modifier.clickable { onActivityClick(activity) }
            )
            HorizontalDivider()
        }
        if (searchResults.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    HaveText("не найдено", color = Color.Gray)
                }
            }
        }
    }
}
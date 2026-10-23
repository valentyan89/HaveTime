package com.example.havetime.presentation.screens.map.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.havetime.R
import com.example.havetime.domain.model.Activity
import com.example.havetime.presentation.common.utils.HaveText
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
internal fun MapSearchResults(
    searchResults: List<Activity>,
    searchItemFormatter: DateTimeFormatter,
    onActivityClick: (Activity) -> Unit
) {
    LazyColumn(
        modifier =
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .zIndex(100f)
    ) {
        items(searchResults) { activity ->
            ListItem(
                headlineContent = { HaveText(activity.title) },
                supportingContent = {
                    val startDateTime =
                        Instant
                            .ofEpochMilli(activity.timeInterval.startTime)
                            .atZone(ZoneId.systemDefault())
                    HaveText(startDateTime.format(searchItemFormatter))
                },
                leadingContent = {
                    Box(
                        modifier = Modifier.size(
                            12.dp
                        ).background(Color(activity.color), CircleShape)
                    )
                },
                modifier =
                Modifier.clickable {
                    onActivityClick(activity)
                }
            )
            HorizontalDivider()
        }
        if (searchResults.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    HaveText(
                        stringResource(R.string.nothing_found),
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

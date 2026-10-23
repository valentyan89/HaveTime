package com.example.havetime.presentation.common.activity.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.havetime.R
import com.example.havetime.presentation.common.utils.HaveText
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
internal fun ActivityDateRange(
    startDate: LocalDate,
    endDate: LocalDate,
    dateFormatter: DateTimeFormatter,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            HaveText(
                stringResource(R.string.start),
                style = MaterialTheme.typography.labelMedium
            )
            TextButton(onClick = {
                onStartDateClick()
            }, contentPadding = PaddingValues(0.dp)) {
                HaveText(startDate.format(dateFormatter))
            }
        }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            HaveText(
                stringResource(R.string.end),
                style = MaterialTheme.typography.labelMedium
            )
            TextButton(onClick = {
                onEndDateClick()
            }, contentPadding = PaddingValues(0.dp)) {
                HaveText(endDate.format(dateFormatter))
            }
        }
    }
}

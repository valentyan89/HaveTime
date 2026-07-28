package com.example.havetime.presentation.screens.calendar.day_week

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.R
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private const val INFINITE_PAGER_ITEMS = 10_000
private const val PAGER_START_INDEX = INFINITE_PAGER_ITEMS / 2

@Composable
fun DayTopBar(
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
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        if (!isSearchActive) {
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
                        .clickable { onGo2Today() },
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

            val monthName = remember(date, currentLocale) {
                date.month.getDisplayName(TextStyle.FULL_STANDALONE, currentLocale)
                    .replaceFirstChar { it.uppercase() }
            }

            Text(
                text = "$monthName ${date.year}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { onTitleClick() }
            )

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onSearchActiveChanged(true) }) {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                }
                IconButton(onClick = onAvatarClick) {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = stringResource(R.string.profile))
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    onSearchActiveChanged(false)
                    onSearchQueryChanged("")
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            }
        }
    }
}
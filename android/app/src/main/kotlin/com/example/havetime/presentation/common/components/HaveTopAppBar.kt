package com.example.havetime.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.havetime.presentation.common.utils.HaveText
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HaveTopAppBar(
    modifier: Modifier = Modifier,
    visibleMonth: YearMonth,
    today: LocalDate,
    currentLocale: Locale,
    onMenuClick: () -> Unit,
    onGoToToday: () -> Unit,
    onTitleClick: () -> Unit,
    onAvatarClick: () -> Unit,
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    onSearchActiveChanged: ((Boolean) -> Unit)? = null,
    onSearchQueryChanged: ((String) -> Unit)? = null
) {
    val searchEnabled =
        onSearchActiveChanged != null && onSearchQueryChanged != null

    val monthName = remember(visibleMonth, currentLocale) {
        visibleMonth.month
            .getDisplayName(TextStyle.FULL_STANDALONE, currentLocale)
            .replaceFirstChar {
                if (it.isLowerCase()) {
                    it.titlecase(currentLocale)
                } else {
                    it.toString()
                }
            }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        if (searchEnabled && isSearchActive) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onSearchActiveChanged.invoke(false)
                        onSearchQueryChanged.invoke("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { onSearchQueryChanged.invoke(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        HaveText(
                            text = stringResource(R.string.search_placeholder)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChanged.invoke("") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.clear_text)
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(R.string.menu)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable(onClick = onGoToToday),
                    contentAlignment = Alignment.Center
                ) {
                    HaveText(
                        text = today.dayOfMonth.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            HaveText(
                text = "$monthName ${visibleMonth.year}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable(onClick = onTitleClick)
            )

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (searchEnabled) {
                    IconButton(
                        onClick = { onSearchActiveChanged.invoke(true) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                }

                IconButton(onClick = onAvatarClick) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(R.string.profile)
                    )
                }
            }
        }
    }
}

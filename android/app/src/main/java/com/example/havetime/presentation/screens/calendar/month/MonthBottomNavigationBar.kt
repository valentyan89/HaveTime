package com.example.havetime.presentation.screens.calendar.month

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.havetime.R
import com.example.havetime.presentation.navigation.Screen

@Composable
fun MonthBottomNavigationBar(
    navController: NavController,
    onCalendarClick: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.calendar)) },
            label = { Text(stringResource(R.string.calendar)) },
            selected = true,
            onClick = onCalendarClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Map, contentDescription = stringResource(R.string.map)) },
            label = { Text(stringResource(R.string.map)) },
            selected = false,
            onClick = { navController.navigate(Screen.Map.route) }
        )
    }
}
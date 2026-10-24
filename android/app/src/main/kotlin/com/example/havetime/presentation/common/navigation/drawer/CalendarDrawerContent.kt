package com.example.havetime.presentation.common.navigation.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.havetime.R
import com.example.havetime.presentation.navigation.Screen
import java.time.Year

@Composable
fun CalendarDrawerContent(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier =
        Modifier
            .fillMaxHeight()
            .width(240.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            CalendarDrawerHeader(onCloseDrawer)

            Spacer(modifier = Modifier.height(16.dp))

            CalendarDrawerItem(
                icon = Icons.Default.Today,
                label = stringResource(R.string.day_screen),
                selected = currentScreen == Screen.Day,
                onClick = {
                    onNavigate(Screen.Day)
                    onCloseDrawer()
                }
            )

            CalendarDrawerItem(
                icon = Icons.Default.CalendarMonth,
                label = stringResource(R.string.month_screen),
                selected = currentScreen == Screen.Month,
                onClick = {
                    onNavigate(Screen.Month)
                    onCloseDrawer()
                }
            )

            CalendarDrawerItem(
                icon = Icons.Default.CalendarViewDay,
                label = stringResource(R.string.year_screen_title),
                selected = currentScreen is Screen.Year,
                onClick = {
                    val currentYear = Year.now().value
                    onNavigate(Screen.Year(currentYear))
                    onCloseDrawer()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            CalendarDrawerItem(
                icon = Icons.Default.Map,
                label = stringResource(R.string.map),
                selected = currentScreen == Screen.Map,
                onClick = {
                    onNavigate(Screen.Map)
                    onCloseDrawer()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            CalendarDrawerItem(
                icon = Icons.Default.Settings,
                label = stringResource(R.string.settings),
                selected = currentScreen == Screen.Settings,
                onClick = {
                    onNavigate(Screen.Settings)
                    onCloseDrawer()
                }
            )
        }
    }
}

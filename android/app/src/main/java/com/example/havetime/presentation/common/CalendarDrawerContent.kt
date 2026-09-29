package com.example.havetime.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.havetime.R
import com.example.havetime.presentation.navigation.Screen
import java.time.Year

@Composable
fun CalendarDrawerContent(
    navController: NavController,
    currentRoute: String,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                IconButton(onClick = onCloseDrawer) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.close_menu),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NavigationDrawerItem(
                icon = {
                    Icon(
                        Icons.Default.Today,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.day_screen),
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                selected = currentRoute == Screen.Day.route,
                onClick = {
                    navController.navigate(Screen.Day.route) {
                        popUpTo(0) { inclusive = false }
                        launchSingleTop = true
                    }
                    onCloseDrawer()
                },
                modifier = Modifier.padding(vertical = 6.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.Transparent
                )
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.month_screen),
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                selected = currentRoute == Screen.Month.route,
                onClick = {
                    navController.navigate(Screen.Month.route) {
                        popUpTo(0) { inclusive = false }
                        launchSingleTop = true
                    }
                    onCloseDrawer()
                },
                modifier = Modifier.padding(vertical = 6.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.Transparent
                )
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        Icons.Default.CalendarViewDay,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.year_screen_title),
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                selected = currentRoute.startsWith(Screen.Year.route),
                onClick = {
                    val currentYear = Year.now().value
                    navController.navigate("${Screen.Year.route}/$currentYear") {
                        popUpTo(0) { inclusive = false }
                        launchSingleTop = true
                    }
                    onCloseDrawer()
                },
                modifier = Modifier.padding(vertical = 6.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.Transparent
                )
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.map),
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                selected = currentRoute == Screen.Map.route,
                onClick = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(0) { inclusive = false }
                        launchSingleTop = true
                    }
                    onCloseDrawer()
                },
                modifier = Modifier.padding(vertical = 6.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.Transparent
                )
            )
        }
    }
}
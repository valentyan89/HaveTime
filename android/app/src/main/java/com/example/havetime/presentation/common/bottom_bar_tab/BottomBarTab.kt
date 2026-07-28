package com.example.havetime.presentation.common.bottom_bar_tab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarTab(val title: String, val icon: ImageVector, val color: Color) {
    data object Profile : BottomBarTab(
        title = "Profile",
        icon = Icons.Rounded.Person,
        color = Color(0xFFFFA574)
    )
    data object Calendar : BottomBarTab(
        title = "Calendar",
        icon = Icons.Rounded.CalendarMonth,
        color = Color(0xFFFA6FFF)
    )
    data object Map : BottomBarTab(
        title = "Map",
        icon = Icons.Rounded.Map,
        color = Color(0xFFADFF64)
    )
    data object Settings : BottomBarTab(
        title = "Settings",
        icon = Icons.Rounded.Settings,
        color = Color(0xFFADFF64)
    )
}

val tabs = listOf(
    BottomBarTab.Calendar,
    BottomBarTab.Map,
    BottomBarTab.Profile
)
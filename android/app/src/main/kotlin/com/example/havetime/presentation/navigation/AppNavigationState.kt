package com.example.havetime.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack

enum class NavigationTab {
    CALENDAR,
    MAP,
    PROFILE,
    SETTINGS
}

val Screen.tab: NavigationTab
    get() = when (this) {
        Screen.Day, Screen.Month, is Screen.Year -> NavigationTab.CALENDAR
        Screen.Map -> NavigationTab.MAP
        Screen.Auth -> NavigationTab.PROFILE
        Screen.Settings -> NavigationTab.SETTINGS
    }

@Stable
class AppNavigationState internal constructor(
    val stacks: Map<NavigationTab, MutableList<NavKey>>,
    private val historyState: MutableState<List<NavigationTab>>
) {
    val tabHistory: List<NavigationTab> get() = historyState.value
    val currentTab: NavigationTab get() = tabHistory.last()
    val currentStack: MutableList<NavKey> get() = stacks.getValue(currentTab)
    val currentScreen: Screen get() = currentStack.last() as Screen

    fun selectTab(tab: NavigationTab) {
        if (tab == currentTab) return
        historyState.value = if (tab == NavigationTab.CALENDAR) {
            listOf(NavigationTab.CALENDAR)
        } else {
            tabHistory.filterNot { it == tab } + tab
        }
    }

    fun navigate(screen: Screen) {
        selectTab(screen.tab)
        if (screen is Screen.Year && Screen.Month !in currentStack) {
            currentStack.navigate(Screen.Month)
        }
        currentStack.navigate(screen)
    }

    fun goBack(): Boolean {
        if (currentStack.goBack()) return true
        if (tabHistory.size <= 1) return false
        historyState.value = tabHistory.dropLast(1)
        return true
    }
}

@Composable
fun rememberAppNavigationState(): AppNavigationState {
    val calendar = rememberNavBackStack(Screen.Day)
    val map = rememberNavBackStack(Screen.Map)
    val profile = rememberNavBackStack(Screen.Auth)
    val settings = rememberNavBackStack(Screen.Settings)
    val history = rememberSaveable(
        stateSaver = listSaver<List<NavigationTab>, String>(
            save = { tabs -> tabs.map { it.name } },
            restore = { names -> names.map { NavigationTab.valueOf(it) } }
        )
    ) { mutableStateOf(listOf(NavigationTab.CALENDAR)) }
    return remember(calendar, map, profile, settings, history) {
        AppNavigationState(
            stacks = mapOf(
                NavigationTab.CALENDAR to calendar,
                NavigationTab.MAP to map,
                NavigationTab.PROFILE to profile,
                NavigationTab.SETTINGS to settings
            ),
            historyState = history
        )
    }
}

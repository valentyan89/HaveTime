package com.example.havetime.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.havetime.presentation.common.navigation.bottom_bar.SharedBottomBarHost
import com.example.havetime.presentation.common.navigation.drawer.CalendarDrawerContent
import com.example.havetime.presentation.screens.auth.AuthScreen
import com.example.havetime.presentation.screens.calendar.day_week.DayScreen
import com.example.havetime.presentation.screens.calendar.month.MonthScreen
import com.example.havetime.presentation.screens.calendar.month.MonthViewModel
import com.example.havetime.presentation.screens.calendar.year.YearScreen
import com.example.havetime.presentation.screens.calendar.year.YearViewModel
import com.example.havetime.presentation.screens.map.MapScreen
import com.example.havetime.presentation.screens.settings.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun CalendarNavHost() {
    val navigation = rememberAppNavigationState()
    val currentScreen = navigation.currentScreen
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val sharedViewModel: MonthViewModel = hiltViewModel()
    val openMenu: () -> Unit = { scope.launch { drawerState.open() } }
    val openAuth: () -> Unit = { navigation.navigate(Screen.Auth) }
    val openMonth: () -> Unit = { navigation.navigate(Screen.Month) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            CalendarDrawerContent(
                currentScreen = currentScreen,
                onNavigate = { navigation.navigate(it) },
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        SharedBottomBarHost(
            currentScreen = currentScreen,
            onNavigate = { navigation.selectTab(it.tab) }
        ) {
            val screenProvider = entryProvider<NavKey> {
                entry<Screen.Day> {
                    DayScreen(
                        onOpenMonth = openMonth,
                        sharedViewModel = sharedViewModel,
                        onAvatarClick = openAuth,
                        onMenuClick = openMenu
                    )
                }
                entry<Screen.Month> {
                    MonthScreen(
                        monthViewModel = sharedViewModel,
                        onAvatarClick = openAuth,
                        onDayClick = { date ->
                            sharedViewModel.selectDate(date)
                            navigation.navigate(Screen.Day)
                        },
                        onYearClick = { navigation.navigate(Screen.Year(it)) },
                        onMenuClick = openMenu
                    )
                }
                entry<Screen.Year> { screen ->
                    val viewModel: YearViewModel = hiltViewModel()
                    androidx.compose.runtime.LaunchedEffect(viewModel) {
                        viewModel.initializeYear(screen.year)
                    }
                    YearScreen(
                        sharedViewModel = sharedViewModel,
                        onOpenMonth = openMonth,
                        viewModel = viewModel,
                        onAvatarClick = openAuth,
                        onMenuClick = openMenu
                    )
                }
                entry<Screen.Map> {
                    MapScreen(
                        onOpenMonth = openMonth,
                        sharedViewModel = sharedViewModel,
                        onAvatarClick = openAuth,
                        onMenuClick = openMenu
                    )
                }
                entry<Screen.Auth> {
                    AuthScreen(onAuthSuccess = {}, onBackClick = { navigation.goBack() })
                }
                entry<Screen.Settings> {
                    SettingsScreen(onBackClick = { navigation.goBack() })
                }
            }

            val decoratedStacks = navigation.stacks.mapValues { (tab, stack) ->
                key(tab) {
                    rememberDecoratedNavEntries(
                        backStack = stack,
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        entryProvider = screenProvider
                    )
                }
            }
            val visibleEntries = navigation.tabHistory.flatMap { decoratedStacks.getValue(it) }
            NavDisplay(
                entries = visibleEntries,
                onBack = { navigation.goBack() }
            )
        }
    }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }
}

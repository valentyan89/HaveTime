package com.example.havetime.presentation.screens.calendar.year

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.havetime.presentation.common.navigation.bottom_bar.GlassyBottomBar
import com.example.havetime.presentation.common.utils.HaveText
import com.example.havetime.presentation.navigation.Screen
import com.example.havetime.presentation.screens.calendar.month.MonthViewModel
import com.example.havetime.presentation.screens.calendar.year.components.YearMonthGrid
import com.example.havetime.presentation.screens.calendar.year.components.YearTopBar
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearScreen(
    navController: NavController,
    sharedViewModel: MonthViewModel,
    onAvatarClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    viewModel: YearViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val today by viewModel.today.collectAsState()

    val currentLocale = LocalConfiguration.current.locales[0]

    val hazeState = remember { HazeState() }
    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        topBar = {
            Spacer(modifier = Modifier.statusBarsPadding())
        },
        bottomBar = {
            GlassyBottomBar(
                navController = navController,
                hazeState = hazeState
            )
        }
    ) { paddingValues ->
        when (val yearState = state) {
            is YearState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is YearState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    HaveText(text = yearState.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is YearState.Success -> {
                Column(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    YearTopBar(
                        currentYear = yearState.currentYear,
                        today = today,
                        onGo2Today = {
                            viewModel.setYear(today.year)
                            sharedViewModel.go2Today()
                        },
                        onPreviousYear = viewModel::goToPreviousYear,
                        onNextYear = viewModel::goToNextYear,
                        onMenuClick = onMenuClick,
                        onAvatarClick = onAvatarClick
                    )

                    YearMonthGrid(
                        monthsData = yearState.monthsData,
                        currentYear = yearState.currentYear,
                        today = today,
                        currentLocale = currentLocale,
                        onMonthClick = { month ->
                            sharedViewModel.setMonth(month)
                            navController.navigate(Screen.Month.route)
                        }
                    )
                }
            }
        }
    }
}

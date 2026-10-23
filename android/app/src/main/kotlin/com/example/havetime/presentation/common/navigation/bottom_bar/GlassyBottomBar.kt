package com.example.havetime.presentation.common.navigation.bottom_bar

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.havetime.presentation.navigation.Screen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

@Composable
fun GlassyBottomBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    hazeState: HazeState,
    tabsList: List<BottomBarTab> = tabs
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedTabIndex =
        remember(currentRoute) {
            when (currentRoute) {
                Screen.Day.route -> 0
                Screen.Map.route -> 1
                Screen.Settings.route -> 2
                else -> 0
            }
        }

    Box(
        modifier = modifier
            .padding(bottom = 16.dp, start = 32.dp, end = 32.dp)
            .navigationBarsPadding()
            .fillMaxWidth()
            .height(64.dp)
            .hazeChild(state = hazeState, shape = CircleShape)
            .border(
                width = Dp.Hairline,
                brush =
                Brush.verticalGradient(
                    colors =
                    listOf(
                        Color.White.copy(alpha = .8f),
                        Color.White.copy(alpha = .2f)
                    )
                ),
                shape = CircleShape
            )
    ) {
        val animatedSelectedTabIndex by animateFloatAsState(
            targetValue = selectedTabIndex.toFloat(),
            label = "animatedSelectedTabIndex",
            animationSpec =
            spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioLowBouncy
            )
        )

        val isBlurSupported = remember { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S }
        val targetColor =
            tabsList.getOrNull(selectedTabIndex)?.color ?: MaterialTheme.colorScheme.primary
        val animatedColor by animateColorAsState(
            targetValue = MaterialTheme.colorScheme.primary,
            label = "animatedColor",
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        )

        BottomBarGlow(tabsList.size, animatedSelectedTabIndex, animatedColor, isBlurSupported)

        BottomBarOutline(tabsList.size, animatedSelectedTabIndex, animatedColor)

        BottomBarTabs(
            tabs = tabsList,
            selectedTab = selectedTabIndex,
            onTabSelected = { tab ->
                val index = tabsList.indexOf(tab)
                val route =
                    when (index) {
                        0 -> Screen.Day.route
                        1 -> Screen.Map.route
                        2 -> Screen.Settings.route
                        else -> null
                    }

                route?.let { target ->
                    if (currentRoute != target) {
                        navController.navigate(target) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        )
    }
}

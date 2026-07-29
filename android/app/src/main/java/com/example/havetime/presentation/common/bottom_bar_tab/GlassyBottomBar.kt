package com.example.havetime.presentation.common.bottom_bar_tab

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.havetime.presentation.navigation.Screen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

@Composable
fun GlassyBottomBar(
    navController: NavController,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    tabsList: List<BottomBarTab> = tabs
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedTabIndex = remember(currentRoute) {
        when (currentRoute) {
            Screen.Day.route -> 0
            Screen.Map.route -> 1
            Screen.Auth.route -> 2
            else -> 0
        }
    }

    Box(
        modifier = Modifier
            .padding(bottom = 16.dp, start = 32.dp, end = 32.dp)
            .navigationBarsPadding()
            .fillMaxWidth()
            .height(64.dp)
            .hazeChild(state = hazeState, shape = CircleShape)
            .border(
                width = Dp.Hairline,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = .8f),
                        Color.White.copy(alpha = .2f),
                    ),
                ),
                shape = CircleShape
            )
    ) {
        val animatedSelectedTabIndex by animateFloatAsState(
            targetValue = selectedTabIndex.toFloat(),
            label = "animatedSelectedTabIndex",
            animationSpec = spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioLowBouncy,
            )
        )

        val isBlurSupported = remember { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S }
        val targetColor = tabsList.getOrNull(selectedTabIndex)?.color ?: MaterialTheme.colorScheme.primary
        val animatedColor by animateColorAsState(
            targetValue = MaterialTheme.colorScheme.primary,
            label = "animatedColor",
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .then(
                    if (isBlurSupported) {
                        Modifier.blur(50.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    } else Modifier
                )
        ) {
            val tabWidth = size.width / tabsList.size
            val centerOffset = Offset(
                x = (tabWidth * animatedSelectedTabIndex) + tabWidth / 2,
                y = size.height / 2
            )

            if (isBlurSupported) {
                drawCircle(
                    color = animatedColor.copy(alpha = .6f),
                    radius = size.height / 2,
                    center = centerOffset
                )
            } else {
                val glowRadius = size.height * 0.9f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            animatedColor.copy(alpha = 0.4f),
                            animatedColor.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = centerOffset,
                        radius = glowRadius
                    ),
                    radius = glowRadius,
                    center = centerOffset
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        ) {
            val path = Path().apply {
                addRoundRect(RoundRect(size.toRect(), CornerRadius(size.height)))
            }
            val length = PathMeasure().apply { setPath(path, false) }.length

            val tabWidth = size.width / tabsList.size
            drawPath(
                path,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        animatedColor.copy(alpha = 0f),
                        animatedColor.copy(alpha = 1f),
                        animatedColor.copy(alpha = 1f),
                        animatedColor.copy(alpha = 0f),
                    ),
                    startX = tabWidth * animatedSelectedTabIndex,
                    endX = tabWidth * (animatedSelectedTabIndex + 1),
                ),
                style = Stroke(
                    width = 6f,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(length / 2, length)
                    )
                )
            )
        }

        BottomBarTabs(
            tabs = tabsList,
            selectedTab = selectedTabIndex,
            onTabSelected = { tab ->
                val index = tabsList.indexOf(tab)
                val route = when (index) {
                    0 -> Screen.Day.route
                    1 -> Screen.Map.route
                    2 -> Screen.Auth.route
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
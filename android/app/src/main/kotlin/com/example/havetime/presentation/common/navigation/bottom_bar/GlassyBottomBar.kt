package com.example.havetime.presentation.common.navigation.bottom_bar

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.havetime.presentation.navigation.Screen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

private const val BOTTOM_BAR_MORPH_DURATION_MS = 280

// The outer margin is 32 dp: a 24 dp slide leaves at least 8 dp to the screen edge.
private val ACTION_EXIT_DISTANCE = 24.dp
private val ACTION_FADE_BAND_WIDTH = 10
    .dp

@Composable
fun GlassyBottomBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    hazeState: HazeState,
    tabsList: List<BottomBarTab> = tabs,
    trailingAction: (@Composable (Float) -> Unit)? = null,
    showTrailingAction: Boolean = trailingAction != null
) {
    val barShape = RoundedCornerShape(32.dp)
    val actionFraction by animateFloatAsState(
        targetValue = if (showTrailingAction) 1f else 0f,
        animationSpec = tween(BOTTOM_BAR_MORPH_DURATION_MS, easing = LinearEasing),
        label = "bottomBarActionFraction"
    )

    val selectedTabIndex =
        remember(currentScreen) {
            when (currentScreen) {
                Screen.Day -> 0
                Screen.Map -> 1
                Screen.Settings -> 2
                else -> 0
            }
        }

    Box(
        modifier = modifier
            .padding(bottom = 16.dp, start = 32.dp, end = 32.dp)
            .navigationBarsPadding()
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .hazeChild(state = hazeState, shape = barShape)
                    .clip(barShape)
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
                        shape = barShape
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

                BottomBarGlow(
                    tabsList.size,
                    animatedSelectedTabIndex,
                    animatedColor,
                    isBlurSupported
                )

                BottomBarOutline(tabsList.size, animatedSelectedTabIndex, animatedColor)

                BottomBarTabs(
                    tabs = tabsList,
                    selectedTab = selectedTabIndex,
                    onTabSelected = { tab ->
                        val index = tabsList.indexOf(tab)
                        val route =
                            when (index) {
                                0 -> Screen.Day
                                1 -> Screen.Map
                                2 -> Screen.Settings
                                else -> null
                            }

                        route?.let { target ->
                            if (currentScreen != target) {
                                onNavigate(target)
                            }
                        }
                    }
                )
            }
            Spacer(Modifier.width(76.dp * actionFraction))
        }
        if (trailingAction != null && actionFraction > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .requiredSize(64.dp)
                    .graphicsLayer {
                        translationX = ACTION_EXIT_DISTANCE.toPx() * (1f - actionFraction)
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                    .drawWithContent {
                        drawContent()
                        val fadeWidth = ACTION_FADE_BAND_WIDTH.toPx()
                        val fadeEnd = (size.width + fadeWidth) * actionFraction
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.White, Color.Transparent),
                                startX = fadeEnd - fadeWidth,
                                endX = fadeEnd
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                trailingAction(actionFraction)
            }
        }
    }
}

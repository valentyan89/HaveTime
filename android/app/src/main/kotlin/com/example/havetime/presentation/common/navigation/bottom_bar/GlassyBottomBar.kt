package com.example.havetime.presentation.common.navigation.bottom_bar

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.havetime.presentation.navigation.Screen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

private const val BOTTOM_BAR_MORPH_DURATION_MS = 600

private val BAR_HEIGHT = 64.dp
private val ACTION_SLOT_SIZE = 64.dp
private val ACTION_RESERVED_WIDTH = 76.dp
private val ACTION_MERGE_TRAVEL = 32.dp

@Composable
fun GlassyBottomBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    hazeState: HazeState,
    tabsList: List<BottomBarTab> = tabs,
    trailingAction: (@Composable (Float) -> Unit)? = null,
    showTrailingAction: Boolean = trailingAction != null,
) {
    val barShape = RoundedCornerShape(32.dp)


    val actionFraction by animateFloatAsState(
        targetValue = if (
            showTrailingAction && trailingAction != null
        ) {
            1f
        } else {
            0f
        },
        animationSpec = tween(
            durationMillis = BOTTOM_BAR_MORPH_DURATION_MS,
            easing = FastOutSlowInEasing,
        ),
        label = "bottomBarActionFraction",
    )

    val mergeProgress = 1f - actionFraction

    val actionTravel =
        ACTION_MERGE_TRAVEL * mergeTravelFraction(mergeProgress)

    val iconAlpha = 1f - ((mergeProgress - 0.35f) / 0.25f).coerceIn(0f, 1f)

    val mergedShape = remember(actionFraction) {
        MergingBottomBarShape(actionFraction)
    }

    val borderBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = .8f),
                Color.White.copy(alpha = .2f),
            )
        )
    }

    val selectedTabIndex = remember(currentScreen) {
        when (currentScreen) {
            Screen.Day -> 0
            Screen.Map -> 1
            Screen.Settings -> 2
            else -> 0
        }
    }

    val animatedSelectedTabIndex by animateFloatAsState(
        targetValue = selectedTabIndex.toFloat(),
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioLowBouncy,
        ),
        label = "animatedSelectedTabIndex",
    )

    val animatedColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primary,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
        ),
        label = "animatedColor",
    )

    val isBlurSupported = remember {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    Box(
        modifier = modifier
            .padding(
                bottom = 16.dp,
                start = 32.dp,
                end = 32.dp,
            )
            .navigationBarsPadding()
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .hazeChild(
                    state = hazeState,
                    shape = mergedShape,
                )
                .border(
                    width = Dp.Hairline,
                    brush = borderBrush,
                    shape = mergedShape,
                )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(BAR_HEIGHT)
                    .clip(barShape),
            ) {
                BottomBarGlow(
                    tabsList.size,
                    animatedSelectedTabIndex,
                    animatedColor,
                    isBlurSupported,
                )

                BottomBarOutline(
                    tabsList.size,
                    animatedSelectedTabIndex,
                    animatedColor,
                )

                BottomBarTabs(
                    tabs = tabsList,
                    selectedTab = selectedTabIndex,
                    onTabSelected = { tab ->
                        val route = when (tabsList.indexOf(tab)) {
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
                    },
                )
            }

            Spacer(
                modifier = Modifier.width(
                    ACTION_RESERVED_WIDTH * actionFraction
                )
            )
        }

        if (trailingAction != null && actionFraction > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .requiredSize(ACTION_SLOT_SIZE)
                    .graphicsLayer {
                        translationX = -actionTravel.toPx()
                        alpha = iconAlpha
                    },
                contentAlignment = Alignment.Center,
            ) {
                trailingAction(actionFraction)
            }
        }
    }
}
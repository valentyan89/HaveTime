package com.example.havetime.presentation.screens.calendar.day_week.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs
import kotlin.math.exp

private const val INFINITE_PAGER_ITEMS = 10_000
private const val PAGER_START_INDEX = INFINITE_PAGER_ITEMS / 2


// Divide inertial speed by this value after each full week. Must be >= 1.
private const val WEEK_SPEED_DIVISOR = 2f
// Continuous friction between weeks. Higher values stop the fling sooner.
private const val WEEK_FLING_FRICTION = 4.2f
// Start snapping before the slow inertial tail (dp per second).
private const val WEEK_SNAP_START_SPEED_DP_PER_SECOND = 120f
// Duration of the final alignment. Lower values align faster.
private const val WEEK_SNAP_DURATION_MS = 160

@Composable
fun WeekCalendarPager(
    date: LocalDate,
    today: LocalDate,
    currentLocale: Locale,
    onSelectDate: (LocalDate) -> Unit
) {
    val pagerState = rememberLazyListState(initialFirstVisibleItemIndex = PAGER_START_INDEX)
    val snapLayoutInfoProvider = remember(pagerState) { SnapLayoutInfoProvider(pagerState) }
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val snapFlingBehavior = remember(snapLayoutInfoProvider, decayAnimationSpec) {
        snapFlingBehavior(
            snapLayoutInfoProvider = snapLayoutInfoProvider,
            decayAnimationSpec = decayAnimationSpec,
            snapAnimationSpec = tween(durationMillis = WEEK_SNAP_DURATION_MS)
        )
    }
    val snapStartSpeed = with(LocalDensity.current) {
        WEEK_SNAP_START_SPEED_DP_PER_SECOND.dp.toPx()
    }
    val weekFlingBehavior = remember(snapFlingBehavior, snapStartSpeed, pagerState) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                var velocity = initialVelocity
                var distanceInWeek = 0f
                var previousFrame = withFrameNanos { it }
                val weekWidth = pagerState.layoutInfo.visibleItemsInfo.firstOrNull()?.size
                    ?.toFloat() ?: 0f
                if (weekWidth <= 0f) {
                    return with(snapFlingBehavior) { performFling(initialVelocity) }
                }

                while (abs(velocity) > snapStartSpeed) {
                    val frame = withFrameNanos { it }
                    val seconds = (frame - previousFrame) / 1_000_000_000f
                    previousFrame = frame
                    val decay = exp(-WEEK_FLING_FRICTION * seconds)
                    val requested = velocity * (1f - decay) / WEEK_FLING_FRICTION
                    val consumed = scrollBy(requested)
                    velocity *= decay
                    distanceInWeek += abs(consumed)
                    while (distanceInWeek >= weekWidth) {
                        distanceInWeek -= weekWidth
                        velocity /= WEEK_SPEED_DIVISOR.coerceAtLeast(1f)
                    }
                    if (abs(requested - consumed) > 0.5f) return velocity
                }
                return with(snapFlingBehavior) { performFling(0f) }
            }
        }
    }

    LaunchedEffect(date) {
        val mondayOfDate =
            date.with(
                java.time.temporal.TemporalAdjusters
                    .previousOrSame(DayOfWeek.MONDAY)
            )
        val mondayOfToday =
            today.with(
                java.time.temporal.TemporalAdjusters
                    .previousOrSame(DayOfWeek.MONDAY)
            )
        val weeksDiff = ChronoUnit.WEEKS.between(mondayOfToday, mondayOfDate).toInt()
        val target = PAGER_START_INDEX + weeksDiff
        if (pagerState.firstVisibleItemIndex != target) {
            pagerState.animateScrollToItem(target)
        }
    }

    LazyRow(
        state = pagerState,
        flingBehavior = weekFlingBehavior,
        modifier =
        Modifier
            .fillMaxWidth()
            .height(70.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(INFINITE_PAGER_ITEMS) { weekIndex ->
            val startOfWeek =
                today
                    .with(
                        java.time.temporal.TemporalAdjusters
                            .previousOrSame(DayOfWeek.MONDAY)
                    ).plusWeeks((weekIndex - PAGER_START_INDEX).toLong())

            Row(modifier = Modifier.fillParentMaxWidth()) {
                (0..6).forEach { dayOffset ->
                    val itemDate = startOfWeek.plusDays(dayOffset.toLong())
                    WeekDayCell(
                        itemDate,
                        date,
                        today,
                        currentLocale,
                        onSelectDate,
                        Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

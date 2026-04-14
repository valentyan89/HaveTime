package com.example.havetime.presentation.screens.day

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.havetime.domain.model.TimeInterval
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun DayScreen(
    intervals: List<TimeInterval>,
    onIntervalCreated: (Int, Int) -> Unit, // Старт и конец (часы)
    onIntervalClick: (TimeInterval) -> Unit
) {
    val hourHeight = 80.dp // Увеличили для удобства "тяги"
    val scrollState = rememberScrollState()

    // Состояние для отрисовки "тянущейся" новой активности
    var dragStartHour by remember { mutableStateOf<Float?>(null) }
    var dragEndHour by remember { mutableStateOf<Float?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {

        // --- ЧАСТЬ 1: ЛЕВЫЕ 10% (Подписи и Скролл) ---
        Box(modifier = Modifier
            .fillMaxHeight()
            .weight(0.1f)
            .background(Color.White)
            .verticalScroll(scrollState)
        ) {
            Column {
                repeat(24) { hour ->
                    Text(
                        text = String.format("%02d:00", hour),
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.height(hourHeight).padding(top = 2.dp, start = 4.dp)
                    )
                }
            }
        }

        // --- ЧАСТЬ 2: ЦЕНТРАЛЬНЫЕ 80% (Зона Активностей и Тяги) ---
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.8f)
                .verticalScroll(scrollState)
                // РЕАЛИЗАЦИЯ ТЯГИ: создание активности движением пальца
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            dragStartHour = offset.y / hourHeight.toPx()
                            dragEndHour = dragStartHour
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragEndHour = (dragEndHour ?: 0f) + (dragAmount.y / hourHeight.toPx())
                        },
                        onDragEnd = {
                            val start = dragStartHour?.toInt() ?: 0
                            val end = dragEndHour?.toInt() ?: (start + 1)
                            if (start != end) {
                                onIntervalCreated(
                                    start.coerceIn(0, 23),
                                    end.coerceIn(start + 1, 24)
                                )
                            }
                            dragStartHour = null
                            dragEndHour = null
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val h = (offset.y / hourHeight.toPx()).toInt()
                        onIntervalCreated(h, h + 1)
                    }
                }
        ) {
            // Сетка линий
            Column {
                repeat(24) { Box(Modifier.height(hourHeight).fillMaxWidth()) { HorizontalDivider(thickness = 0.5.dp) } }
            }

            // РЕАЛИЗАЦИЯ НАЛОЖЕНИЯ: Алгоритм Google Calendar с вашими правками
            val sorted = intervals.sortedBy { it.start }
            val columns = calculateColumns(sorted)

            sorted.forEach { interval ->
                val colInfo = columns[interval.id] ?: Triple(0, 1, 0)
                val startMin = interval.start.hour * 60 + interval.start.minute
                val duration = ChronoUnit.MINUTES.between(interval.start, interval.end).toInt()

                // Расчет ширины: если начались в одно время - 50/50, если позже - сдвиг 8dp
                val baseWidthPercent = 1f / colInfo.second
                val xOffset = (colInfo.first * 8).dp // Сдвиг 8dp для тех, кто позже

                Box(modifier = Modifier
                    .fillMaxWidth(baseWidthPercent)
                    .offset(x = xOffset, y = (startMin * (hourHeight.value / 60f)).dp)
                    .height((duration * (hourHeight.value / 60f)).dp)
                    .padding(1.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(interval.color).copy(alpha = 0.9f))
                    .clickable { onIntervalClick(interval) }
                    .padding(4.dp)
                    .zIndex(startMin.toFloat()) // Чем позже, тем выше (на переднем плане)
                ) {
                    Text(interval.title, fontSize = 11.sp, color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, maxLines = 1)
                }
            }

            // Отрисовка "Призрака" во время тяги пальцем
            if (dragStartHour != null && dragEndHour != null) {
                val s = dragStartHour!!
                val e = dragEndHour!!
                val top = if (s < e) s else e
                val bottom = if (s < e) e else s
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (top * hourHeight.value).dp)
                    .height(((bottom - top) * hourHeight.value).dp)
                    .background(Color.Blue.copy(0.2f))
                )
            }
        }

        // --- ЧАСТЬ 3: ПРАВЫЕ 10% (Чистый Скролл) ---
        Box(modifier = Modifier
            .fillMaxHeight()
            .weight(0.1f)
            .background(Color.White)
            .verticalScroll(scrollState) // Тот же стейт - синхронное движение
        )
    }
}

/**
 * Логика наложения:
 * Triple(Индекс_колонки, Всего_колонок, Сдвиг)
 */
fun calculateColumns(intervals: List<TimeInterval>): Map<String, Triple<Int, Int, Int>> {
    val results = mutableMapOf<String, Triple<Int, Int, Int>>()
    val groups = mutableListOf<MutableList<TimeInterval>>()

    // Группировка пересекающихся
    for (interval in intervals) {
        var added = false
        for (group in groups) {
            if (group.any { it.start < interval.end && interval.start < it.end }) {
                group.add(interval)
                added = true; break
            }
        }
        if (!added) groups.add(mutableListOf(interval))
    }

    for (group in groups) {
        group.sortBy { it.start }
        group.forEachIndexed { index, interval ->
            // Если старт одинаковый - делим пополам (1/group.size)
            // Если старт разный - индекс определяет сдвиг
            results[interval.id] = Triple(index, group.size, index * 8)
        }
    }
    return results
}
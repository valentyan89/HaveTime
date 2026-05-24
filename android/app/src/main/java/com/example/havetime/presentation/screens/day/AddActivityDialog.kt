package com.example.havetime.presentation.screens.day

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.TimeInterval
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Locale
import java.util.UUID

@Composable
fun AddActivityDialog(
    editingActivity: Activity? = null,
    initialDate: LocalDate,
    initialStartTime: LocalTime = LocalTime.of(12, 0),
    initialEndTime: LocalTime = LocalTime.of(13, 0),
    onDismiss: () -> Unit,
    onConfirm: (Activity) -> Unit,
    onDelete: (String) -> Unit
) {
    var title by remember { mutableStateOf(editingActivity?.title ?: "") }

    var startH by remember { mutableIntStateOf(editingActivity?.timeInterval?.start?.hour ?: initialStartTime.hour) }
    var startM by remember { mutableIntStateOf(editingActivity?.timeInterval?.start?.minute ?: initialStartTime.minute) }
    var endH by remember { mutableIntStateOf(editingActivity?.timeInterval?.end?.hour ?: initialEndTime.hour) }
    var endM by remember { mutableIntStateOf(editingActivity?.timeInterval?.end?.minute ?: initialEndTime.minute) }

    var selectedColor by remember { mutableStateOf(Color(editingActivity?.color ?: 0xFF854CE5.toInt())) }

    val colorRows = listOf(
        listOf(Color(0xFFEF5350), Color(0xFFEC407A), Color(0xFFAB47BC), Color(0xFF7E57C2), Color(0xFF5C6BC0)),
        listOf(Color(0xFF42A5F5), Color(0xFF26A69A), Color(0xFF66BB6A), Color(0xFFFFCA28), Color(0xFFFF7043))
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(text = if (editingActivity == null) "Новая активность" else "Изменить")
                if (editingActivity != null) {
                    IconButton(onClick = { onDelete(editingActivity.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                    }
                }
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )

                val isMultiDay = endH >= 24 || (LocalTime.of(endH % 24, endM % 60).isBefore(LocalTime.of(startH % 24, startM % 60)))
                if (isMultiDay) {
                    Text(
                        "Завершится на следующий день",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = String.format(Locale.getDefault(), "Начало: %02d:%02d", startH % 24, startM % 60),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                TimeWheelPicker(startH % 24, startM % 60) { h, m -> startH = h; startM = m }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = String.format(Locale.getDefault(), "Конец: %02d:%02d", endH % 24, endM % 60),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                TimeWheelPicker(endH % 24, endM % 60) { h, m -> endH = h; endM = m }

                Spacer(Modifier.height(20.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorRows.forEach { row ->
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                            row.forEach { colorItem ->
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colorItem)
                                        .clickable { selectedColor = colorItem }
                                        .padding(4.dp)
                                ) {
                                    if (selectedColor == colorItem) {
                                        Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(4.dp)))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val start = LocalDateTime.of(initialDate, LocalTime.of(startH % 24, startM % 60))
                var end = LocalDateTime.of(initialDate, LocalTime.of(endH % 24, endM % 60))

                if (end.isBefore(start) || endH >= 24) {
                    end = end.plusDays(1)
                }

                if (end.isBefore(start.plusMinutes(10))) end = start.plusMinutes(10)

                onConfirm(
                    editingActivity?.copy(
                        title = title.ifEmpty { "Активность" },
                        timeInterval = TimeInterval(start, end),
                        color = selectedColor.toArgb()
                    ) ?: Activity(
                        id = UUID.randomUUID().toString(),
                        title = title.ifEmpty { "Активность" },
                        timeInterval = TimeInterval(start, end),
                        color = selectedColor.toArgb(),
                        offsetX = 0f,
                        paddingEnd = 16f
                    )
                )
            }) { Text("ОК") }
        }
    )
}

@Composable
fun TimeWheelPicker(hour: Int, minute: Int, onTimeChange: (Int, Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WheelColumn(24, hour, "ч") { onTimeChange(it, minute) }
        Spacer(Modifier.width(20.dp))
        WheelColumn(60, minute, "м") { onTimeChange(hour, it) }
    }
}

@Composable
fun WheelColumn(count: Int, initialValue: Int, label: String, onValueChange: (Int) -> Unit) {
    val itemHeight = 34.dp
    val totalItems = 10000
    val startIndex = (totalItems / 2) - ((totalItems / 2) % count) + initialValue
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (startIndex - 1).coerceAtLeast(0))
    val snapBehavior = rememberSnapFlingBehavior(listState)

    val currentSelected by remember {
        derivedStateOf { (listState.firstVisibleItemIndex + 1) % count }
    }

    LaunchedEffect(currentSelected) {
        onValueChange(currentSelected)
    }

    Box(Modifier.width(65.dp).height(itemHeight * 3), Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
        )

        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(totalItems) { index ->
                val displayValue = index % count
                val isSelected = (listState.firstVisibleItemIndex + 1) == index

                Box(Modifier.fillMaxWidth().height(itemHeight), Alignment.Center) {
                    Text(
                        text = if(label == "м") String.format(Locale.getDefault(), "%02d%s", displayValue, label) else "$displayValue$label",
                        fontSize = if (isSelected) 18.sp else 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
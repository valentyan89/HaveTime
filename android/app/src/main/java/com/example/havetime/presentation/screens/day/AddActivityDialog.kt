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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.domain.model.TimeInterval
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@Composable
fun AddActivityDialog(
    editingInterval: TimeInterval? = null,
    initialDate: LocalDate,
    initialStartTime: LocalTime = LocalTime.of(12, 0),
    initialEndTime: LocalTime = LocalTime.of(13, 0),
    onDismiss: () -> Unit,
    onConfirm: (TimeInterval) -> Unit,
    onDelete: (String) -> Unit
) {
    var title by remember { mutableStateOf(editingInterval?.title ?: "") }
    var startH by remember { mutableIntStateOf(editingInterval?.start?.hour ?: initialStartTime.hour) }
    var startM by remember { mutableIntStateOf(editingInterval?.start?.minute ?: initialStartTime.minute) }
    var endH by remember { mutableIntStateOf(editingInterval?.end?.hour ?: initialEndTime.hour) }
    var endM by remember { mutableIntStateOf(editingInterval?.end?.minute ?: initialEndTime.minute) }

    var selectedColor by remember { mutableStateOf(Color(editingInterval?.color ?: 0xFF854CE5.toInt())) }
    val colors = listOf(Color(0xFFC73937), Color(0xFF42A5F5), Color(0xFF4CAF50), Color(0xFF854CE5), Color(0xFFFFCA31))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(if (editingInterval == null) "Новая активность" else "Изменить")
                if (editingInterval != null) {
                    IconButton(onClick = { onDelete(editingInterval.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
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

                Spacer(Modifier.height(16.dp))

                Text("Начало: ${String.format("%02d:%02d", startH, startM)}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                TimeWheelPicker(
                    hour = startH,
                    minute = startM,
                    onTimeChange = { h, m -> startH = h; startM = m }
                )

                Spacer(Modifier.height(16.dp))

                Text("Конец: ${String.format("%02d:%02d", endH, endM)}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                TimeWheelPicker(
                    hour = endH,
                    minute = endM,
                    onTimeChange = { h, m -> endH = h; endM = m }
                )

                Spacer(Modifier.height(20.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    colors.forEach { colorItem ->
                        Box(
                            Modifier.size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colorItem)
                                .clickable { selectedColor = colorItem }
                                .padding(4.dp)
                                .then(if(selectedColor == colorItem) Modifier.background(Color.Black.copy(0.1f)) else Modifier)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val start = LocalDateTime.of(initialDate, LocalTime.of(startH.coerceIn(0, 23), startM.coerceIn(0, 59)))
                val end = if (endH >= 24) LocalDateTime.of(initialDate.plusDays(1), LocalTime.MIDNIGHT)
                else LocalDateTime.of(initialDate, LocalTime.of(endH.coerceIn(0, 23), endM.coerceIn(0, 59)))

                onConfirm(TimeInterval(
                    id = editingInterval?.id ?: UUID.randomUUID().toString(),
                    title = title.ifEmpty { "Активность" },
                    start = start, end = end, color = android.graphics.Color.argb(
                        (selectedColor.alpha * 255).toInt(), (selectedColor.red * 255).toInt(),
                        (selectedColor.green * 255).toInt(), (selectedColor.blue * 255).toInt()
                    )
                ))
            }) { Text("ОК") }
        }
    )
}

@Composable
fun TimeWheelPicker(
    hour: Int,
    minute: Int,
    onTimeChange: (Int, Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WheelColumn(
            count = 24,
            initialValue = hour,
            label = "ч",
            onValueChange = { onTimeChange(it, minute) }
        )
        Spacer(Modifier.width(20.dp))
        WheelColumn(
            count = 60,
            initialValue = minute,
            label = "м",
            onValueChange = { onTimeChange(hour, it) }
        )
    }
}

@Composable
fun WheelColumn(
    count: Int,
    initialValue: Int,
    label: String,
    onValueChange: (Int) -> Unit
) {
    val itemHeight = 34.dp
    val totalItems = 10000
    val startIndex = (totalItems / 2) - ((totalItems / 2) % count) + initialValue
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex - 1)
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val currentSelected by remember {
        derivedStateOf {
            (listState.firstVisibleItemIndex + 1) % count
        }
    }
    LaunchedEffect(currentSelected) {
        onValueChange(currentSelected)
    }
    Box(
        modifier = Modifier.width(60.dp).height(itemHeight * 3),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(itemHeight)
                .background(MaterialTheme.colorScheme.primary.copy(0.1f), RoundedCornerShape(8.dp))
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

                Box(
                    modifier = Modifier.fillMaxWidth().height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if(label == "м") String.format("%02d%s", displayValue, label) else "$displayValue$label",
                        fontSize = if (isSelected) 17.sp else 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.alpha(if (isSelected) 1f else 0.4f)
                    )
                }
            }
        }
    }
}
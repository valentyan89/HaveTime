package com.example.havetime.presentation.screens.day

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.havetime.domain.model.TimeInterval
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.roundToInt

@Composable
fun AddActivityDialog(
    initialStartTime: LocalTime = LocalTime.of(12, 0),
    initialEndTime: LocalTime = LocalTime.of(13, 0),
    editingInterval: TimeInterval? = null, // Если не null - мы редактируем
    initialDate: LocalDate,
    initialHour: Int,
    onDismiss: () -> Unit,
    onConfirm: (TimeInterval) -> Unit,
    onDelete: (String) -> Unit
) {
    var title by remember { mutableStateOf(editingInterval?.title ?: "") }
    var selectedColor by remember { mutableStateOf(Color(editingInterval?.color ?: 0xFF42A5F5.toInt())) }
    var startH by remember { mutableFloatStateOf(initialStartTime.hour + initialStartTime.minute / 60f) }
    var endH by remember { mutableFloatStateOf(initialEndTime.hour + initialEndTime.minute / 60f) }
    // Сетка цветов (спектр)
    val spectrum = listOf(
        Color(0xFFEF5350), Color(0xFFEC407A), Color(0xFFAB47BC), Color(0xFF7E57C2),
        Color(0xFF5C6BC0), Color(0xFF42A5F5), Color(0xFF29B6F6), Color(0xFF26C6DA),
        Color(0xFF26A69A), Color(0xFF66BB6A), Color(0xFF9CCC65), Color(0xFFD4E157)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (editingInterval == null) "Новая активность" else "Правка")
                if (editingInterval != null) {
                    IconButton(onClick = { onDelete(editingInterval.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = Color.Red)
                    }
                }
            }
        },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Название") })
                Spacer(Modifier.height(16.dp))

                Text("Время: ${startH.roundToInt()}:00 - ${endH.roundToInt()}:00")
                Slider(value = startH, onValueChange = { startH = it }, valueRange = 0f..23f, steps = 23)
                Slider(value = endH, onValueChange = { if (it > startH) endH = it }, valueRange = 1f..24f, steps = 23)

                Text("Цвет (Спектр):")
                FlowRow(modifier = Modifier.padding(top = 8.dp), maxItemsInEachRow = 6) {
                    spectrum.forEach { color ->
                        Box(
                            Modifier.size(34.dp).padding(2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color)
                                .clickable { selectedColor = color }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val start = LocalDateTime.of(initialDate, LocalTime.of(startH.roundToInt(), 0))
                val end = if (endH >= 24) LocalDateTime.of(initialDate.plusDays(1), LocalTime.MIDNIGHT)
                else LocalDateTime.of(initialDate, LocalTime.of(endH.roundToInt(), 0))

                onConfirm(TimeInterval(
                    id = editingInterval?.id ?: java.util.UUID.randomUUID().toString(),
                    title = title, start = start, end = end,
                    color = android.graphics.Color.argb(
                        (selectedColor.alpha * 255).toInt(),
                        (selectedColor.red * 255).toInt(),
                        (selectedColor.green * 255).toInt(),
                        (selectedColor.blue * 255).toInt()
                    )
                ))
            }) { Text("Сохранить") }
        }
    )
}
package com.example.havetime.presentation.screens.day

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun AddActivityDialog(
    initialDate: Calendar,
    onDismiss: () -> Unit,
    onConfirm: (String, Calendar, Int, Int, Color) -> Unit
) {
    var title by remember { mutableStateOf("") }
    // Используем Float для слайдеров и переводим в Int через roundToInt()
    var startHourFloat by remember { mutableFloatStateOf(10f) }
    var endHourFloat by remember { mutableFloatStateOf(12f) }

    val colors = listOf(Color(0xFFEF5350), Color(0xFF42A5F5), Color(0xFF66BB6A), Color(0xFFAB47BC), Color(0xFFFFA726))
    var selectedColor by remember { mutableStateOf(colors[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая активность") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Начало: ${startHourFloat.roundToInt()}:00")
                Slider(
                    value = startHourFloat,
                    onValueChange = { startHourFloat = it },
                    valueRange = 0f..23f,
                    steps = 22
                )

                Text("Конец: ${endHourFloat.roundToInt()}:00")
                Slider(
                    value = endHourFloat,
                    onValueChange = {
                        // Конец не может быть раньше начала
                        if (it > startHourFloat) endHourFloat = it
                    },
                    valueRange = 1f..24f,
                    steps = 23
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(color)
                                .clickable { selectedColor = color }
                                .let {
                                    if (selectedColor == color) it.background(color.copy(alpha = 0.5f)) else it
                                }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    title,
                    initialDate,
                    startHourFloat.roundToInt(),
                    endHourFloat.roundToInt(),
                    selectedColor
                )
            }) { Text("Создать") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
package com.example.havetime.presentation.screens.day

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.domain.model.TimeInterval

@Composable
fun DayScreen(
    intervals: List<TimeInterval>,
    onAddInterval: (String, Int, Int, Color) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableIntStateOf(0) }

    if (showDialog) {
        AddIntervalDialog(
            initialHour = selectedHour,
            onDismiss = { showDialog = false },
            onConfirm = { title, startH, endH, color ->
                onAddInterval(title, startH, endH, color)
                showDialog = false
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column {
            repeat(24) { hour ->
                Row(
                    modifier = Modifier.height(60.dp).fillMaxWidth()
                        .clickable { selectedHour = hour; showDialog = true }
                ) {
                    Text("${hour}:00", fontSize = 12.sp, modifier = Modifier.width(50.dp).padding(8.dp))
                    HorizontalDivider(modifier = Modifier.padding(top = 16.dp), thickness = 0.5.dp)
                }
            }
        }
        intervals.forEach { interval ->
            val startMin = interval.start.get(Calendar.HOUR_OF_DAY) * 60
            val duration = ((interval.end.timeInMillis - interval.start.timeInMillis) / 60000).toInt()
            Box(modifier = Modifier.padding(start = 60.dp, end = 8.dp).offset(y = startMin.dp).height(duration.dp)
                .fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(Color(interval.color).copy(0.7f)).padding(4.dp)
            ) { Text(interval.title, color = Color.White, fontSize = 11.sp) }
        }
    }
}

@Composable
fun AddIntervalDialog(initialHour: Int, onDismiss: () -> Unit, onConfirm: (String, Int, Int, Color) -> Unit) {
    var title by remember { mutableStateOf("") }
    var endHour by remember { mutableIntStateOf(initialHour + 1) }
    val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Magenta)
    var selectedColor by remember { mutableStateOf(colors[0]) }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Добавить") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Название") })
                Text("До $endHour:00", modifier = Modifier.padding(top = 8.dp))
                Slider(value = endHour.toFloat(), onValueChange = { endHour = it.toInt() }, valueRange = (initialHour + 1).toFloat()..24f)
                Row { colors.forEach { c -> Box(Modifier.size(30.dp).background(c).clickable { selectedColor = c }.padding(4.dp)) } }
            }
        },
        confirmButton = { Button(onClick = { onConfirm(title, initialHour, endHour, selectedColor) }) { Text("OK") } }
    )
}
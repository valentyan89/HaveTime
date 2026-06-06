package com.example.havetime.presentation.screens.calendar.day_week



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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.R
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.TimeInterval
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.layout.fillMaxSize

private val EventColors = listOf(
    R.color.event_red,
    R.color.event_blue,
    R.color.event_green,
    R.color.event_purple,
    R.color.event_yellow
)

@Composable
fun AddActivityDialog(
    editingActivity: Activity? = null,
    initialDate: LocalDate,
    initialStartTime: LocalTime = LocalTime.of(12, 0),
    initialEndTime: LocalTime = LocalTime.of(13, 0),
    onDismiss: () -> Unit,
    onConfirm: (Activity) -> Unit,
    onDelete: (Int) -> Unit
) {
    val editingInterval = editingActivity?.timeInterval

    var title by remember { mutableStateOf(editingActivity?.title ?: "") }

    val initialStartHour = editingInterval?.startTime?.let {
        LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(it), ZoneId.systemDefault()).hour
    } ?: initialStartTime.hour

    val initialStartMinute = editingInterval?.startTime?.let {
        LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(it), ZoneId.systemDefault()).minute
    } ?: initialStartTime.minute

    val initialEndHour = editingInterval?.endTime?.let {
        LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(it), ZoneId.systemDefault()).hour
    } ?: initialEndTime.hour

    val initialEndMinute = editingInterval?.endTime?.let {
        LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(it), ZoneId.systemDefault()).minute
    } ?: initialEndTime.minute

    var startH by remember { mutableIntStateOf(initialStartHour) }
    var startM by remember { mutableIntStateOf(initialStartMinute) }
    var endH by remember { mutableIntStateOf(initialEndHour) }
    var endM by remember { mutableIntStateOf(initialEndMinute) }

    val defaultColor = colorResource(R.color.event_purple)
    var selectedColor by remember(editingActivity) {
        mutableStateOf(
            editingActivity?.color?.let { Color(it) } ?: defaultColor
        )
    }

    val defaultTitle = stringResource(R.string.default_activity_title)
    val okText = stringResource(R.string.ok)
    val cancelText = stringResource(R.string.cancel)
    val newActivityText = stringResource(R.string.new_activity)
    val editActivityText = stringResource(R.string.edit_activity)
    val titleLabel = stringResource(R.string.title)
    val startLabel = stringResource(R.string.start)
    val endLabel = stringResource(R.string.end)


    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (editingActivity == null)
                        newActivityText
                    else
                        editActivityText
                )
                if (editingActivity != null) {
                    IconButton(onClick = { onDelete(editingActivity.id) }) {
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
                    label = { Text(titleLabel) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "$startLabel: ${String.format("%02d:%02d", startH, startM)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                TimeWheelPicker(
                    hour = startH,
                    minute = startM,
                    onTimeChange = { h, m -> startH = h; startM = m }
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "$endLabel: ${String.format("%02d:%02d", endH, endM)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                TimeWheelPicker(
                    hour = endH,
                    minute = endM,
                    onTimeChange = { h, m -> endH = h; endM = m }
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EventColors.forEach { colorRes ->
                        val color = colorResource(colorRes)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color)
                                .clickable { selectedColor = color }
                                .then(
                                    if (selectedColor == color)
                                        Modifier.background(Color.Black.copy(alpha = 0.1f))
                                    else
                                        Modifier
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val startDateTime = LocalDateTime.of(initialDate, LocalTime.of(startH.coerceIn(0, 23), startM.coerceIn(0, 59)))
                    val endDateTime = if (endH >= 24)
                        LocalDateTime.of(initialDate.plusDays(1), LocalTime.MIDNIGHT)
                    else
                        LocalDateTime.of(initialDate, LocalTime.of(endH.coerceIn(0, 23), endM.coerceIn(0, 59)))


                    val activity = Activity(
                        id = editingActivity?.id ?: 0,
                        userId = editingActivity?.userId ?: 0,
                        title = title.ifEmpty { defaultTitle },
                        timeInterval = TimeInterval(
                            startTime = startDateTime.toInstant(java.time.ZoneOffset.UTC).toEpochMilli(),
                            endTime = endDateTime.toInstant(java.time.ZoneOffset.UTC).toEpochMilli()
                        ),
                        color = selectedColor.toArgb(),
                        location = editingActivity?.location,
                        isSynced = false,
                        isDeleted = false,
                        lastTimeModified = System.currentTimeMillis()
                    )
                    onConfirm(activity)
                }
            ) {
                Text(okText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText)
            }
        }
    )
}

@Composable
fun TimeWheelPicker(
    hour: Int,
    minute: Int,
    onTimeChange: (Int, Int) -> Unit
) {
    val hourLabel = stringResource(R.string.hour_label)
    val minuteLabel = stringResource(R.string.minute_label)

    Row(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WheelColumn(
            count = 24,
            initialValue = hour,
            label = hourLabel,
            onValueChange = { onTimeChange(it, minute) }
        )
        Spacer(Modifier.width(20.dp))
        WheelColumn(
            count = 60,
            initialValue = minute,
            label = minuteLabel,
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
    val minuteLabel = stringResource(R.string.minute_label)
    val isMinuteColumn = label == minuteLabel

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
                        text = if (isMinuteColumn)
                            String.format("%02d%s", displayValue, label)
                        else
                            "$displayValue$label",
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
package com.example.havetime.presentation.common.activity

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.havetime.R
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.Location
import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.presentation.common.activity.components.ActivityColorPicker
import com.example.havetime.presentation.common.activity.components.ActivityDatePicker
import com.example.havetime.presentation.common.activity.components.ActivityDateRange
import com.example.havetime.presentation.common.activity.components.ActivityLocationSelection
import com.example.havetime.presentation.common.pickers.TimeWheelPicker
import com.example.havetime.presentation.common.utils.HaveText
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityDialog(
    editingActivity: Activity? = null,
    initialDate: LocalDate,
    initialStartTime: LocalTime,
    initialEndTime: LocalTime,
    initialLocation: Location? = null,
    onDismiss: () -> Unit,
    onConfirm: (Activity) -> Unit,
    onDelete: (Int) -> Unit
) {
    var title by remember(editingActivity?.id) { mutableStateOf(editingActivity?.title ?: "") }

    val currentLocale = LocalConfiguration.current.locales[0]
    val dateFormatter =
        remember(currentLocale) {
            DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(currentLocale)
        }

    val startDT =
        if (editingActivity != null) {
            Instant.ofEpochMilli(
                editingActivity.timeInterval.startTime
            ).atZone(ZoneId.systemDefault()).toLocalDateTime()
        } else {
            LocalDateTime.of(initialDate, initialStartTime)
        }

    val endDT =
        if (editingActivity != null) {
            Instant.ofEpochMilli(
                editingActivity.timeInterval.endTime
            ).atZone(ZoneId.systemDefault()).toLocalDateTime()
        } else {
            LocalDateTime.of(initialDate, initialEndTime)
        }

    var startDate by remember(editingActivity?.id) { mutableStateOf(startDT.toLocalDate()) }
    var startH by remember(editingActivity?.id) { mutableIntStateOf(startDT.hour) }
    var startM by remember(editingActivity?.id) { mutableIntStateOf(startDT.minute) }

    var endDate by remember(editingActivity?.id) { mutableStateOf(endDT.toLocalDate()) }
    var endH by remember(editingActivity?.id) { mutableIntStateOf(endDT.hour) }
    var endM by remember(editingActivity?.id) { mutableIntStateOf(endDT.minute) }

    var selectedColor by remember(editingActivity?.id, editingActivity?.color) {
        mutableStateOf(Color(editingActivity?.color ?: 0xFF854CE5.toInt()))
    }

    var showMapSelection by remember { mutableStateOf(false) }
    var tempLocation by remember(editingActivity?.id, initialLocation) {
        mutableStateOf(initialLocation ?: editingActivity?.location)
    }

    LaunchedEffect(startH, startM, endH, endM) {
        val st = LocalTime.of(startH % 24, startM % 60)
        val et = LocalTime.of(endH % 24, endM % 60)
        if (et.isBefore(st) || endH >= 24) {
            if (endDate == startDate) {
                endDate = startDate.plusDays(1)
            }
        } else if (endDate == startDate.plusDays(1) && endH < 24) {
            endDate = startDate
        }
    }

    val filterColor = MaterialTheme.colorScheme.primary
    val filterIntensity = 0.2f

    val colorRows =
        listOf(
            listOf(
                Color(0xFFEF5350),
                Color(0xFFEC407A),
                Color(0xFFAB47BC),
                Color(0xFF7E57C2),
                Color(0xFF5C6BC0)
            ),
            listOf(
                Color(0xFF42A5F5),
                Color(0xFF26A69A),
                Color(0xFF66BB6A),
                Color(0xFFFFCA28),
                Color(0xFFFF7043)
            )
        ).map { row ->
            row.map { color ->
                lerp(color, filterColor, filterIntensity)
            }
        }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    if (showStartDatePicker) {
        ActivityDatePicker(
            date = startDate,
            onDismiss = { showStartDatePicker = false },
            onDateSelected = {
                startDate = it
                if (endDate.isBefore(startDate)) endDate = startDate
            }
        )
    }

    if (showEndDatePicker) {
        ActivityDatePicker(
            date = endDate,
            onDismiss = { showEndDatePicker = false },
            onDateSelected = {
                endDate = it
                if (endDate.isBefore(startDate)) startDate = endDate
            }
        )
    }

    val defaultActivityTitle = stringResource(R.string.default_activity_title)

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            HaveText(
                text =
                if (showMapSelection) {
                    stringResource(R.string.select_location)
                } else if (editingActivity == null) {
                    stringResource(R.string.new_activity)
                } else {
                    stringResource(R.string.edit_activity)
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        text = {
            Crossfade(targetState = showMapSelection, label = "") { isMap ->
                if (isMap) {
                    ActivityLocationSelection(
                        tempLocation = tempLocation,
                        selectedColor = selectedColor,
                        onLocationPicked = { tempLocation = it },
                        onApply = { showMapSelection = false }
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { HaveText(stringResource(R.string.title)) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(16.dp))

                        ActivityDateRange(
                            startDate = startDate,
                            endDate = endDate,
                            dateFormatter = dateFormatter,
                            onStartDateClick = { showStartDatePicker = true },
                            onEndDateClick = { showEndDatePicker = true }
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { showMapSelection = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            HaveText(
                                text =
                                if (tempLocation != null) {
                                    stringResource(
                                        R.string.location_selected
                                    )
                                } else {
                                    stringResource(R.string.select_location)
                                }
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        HaveText(
                            text = "${stringResource(
                                R.string.start_time
                            )}: ${String.format(
                                currentLocale,
                                "%02d:%02d",
                                startH % 24,
                                startM % 60
                            )}",
                            style = MaterialTheme.typography.labelSmall
                        )
                        TimeWheelPicker(startH % 24, startM % 60) { h, m ->
                            startH = h
                            startM = m
                        }

                        Spacer(Modifier.height(12.dp))

                        HaveText(
                            text = "${stringResource(
                                R.string.end_time
                            )}: ${String.format(currentLocale, "%02d:%02d", endH % 24, endM % 60)}",
                            style = MaterialTheme.typography.labelSmall
                        )
                        TimeWheelPicker(endH % 24, endM % 60) { h, m ->
                            endH = h
                            endM = m
                        }

                        Spacer(Modifier.height(20.dp))

                        ActivityColorPicker(colorRows, selectedColor) { selectedColor = it }
                    }
                }
            }
        },
        confirmButton = {
            if (!showMapSelection) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (editingActivity != null) {
                            TextButton(onClick = { onDelete(editingActivity.id) }) {
                                HaveText(
                                    stringResource(R.string.delete),
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                        }
                        TextButton(onClick = onDismiss) {
                            HaveText(stringResource(R.string.cancel))
                        }
                    }

                    Button(
                        onClick = {
                            val st = LocalDateTime.of(
                                startDate,
                                LocalTime.of(
                                    startH % 24,
                                    startM % 60
                                )
                            )
                            val et = LocalDateTime.of(endDate, LocalTime.of(endH % 24, endM % 60))

                            val updatedActivity =
                                Activity(
                                    id = editingActivity?.id ?: 0,
                                    userId = editingActivity?.userId ?: 1,
                                    title = title.ifEmpty { defaultActivityTitle },
                                    timeInterval =
                                    TimeInterval(
                                        st.atZone(
                                            ZoneId.systemDefault()
                                        ).toInstant().toEpochMilli(),
                                        et.atZone(
                                            ZoneId.systemDefault()
                                        ).toInstant().toEpochMilli()
                                    ),
                                    color = selectedColor.toArgb(),
                                    location = tempLocation,
                                    lastTimeModified = System.currentTimeMillis()
                                )
                            onConfirm(updatedActivity)
                        },
                        shape = RoundedCornerShape(24.dp)
                    ) { HaveText(stringResource(R.string.ok)) }
                }
            }
        },
        dismissButton = null
    )
}

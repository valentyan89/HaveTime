package com.example.havetime.presentation.common.pickers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.havetime.R

@Composable
fun TimeWheelPicker(hour: Int, minute: Int, onTimeChange: (Int, Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WheelColumn(24, hour, stringResource(R.string.hour_label)) { onTimeChange(it, minute) }
        Spacer(Modifier.width(20.dp))
        WheelColumn(60, minute, stringResource(R.string.minute_label)) { onTimeChange(hour, it) }
    }
}

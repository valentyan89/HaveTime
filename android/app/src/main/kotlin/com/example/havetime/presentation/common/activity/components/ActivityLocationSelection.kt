package com.example.havetime.presentation.common.activity.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.havetime.R
import com.example.havetime.domain.model.Location
import com.example.havetime.presentation.common.pickers.LocationPickerView
import com.example.havetime.presentation.common.utils.HaveText

@Composable
internal fun ActivityLocationSelection(
    tempLocation: Location?,
    selectedColor: Color,
    onLocationPicked: (Location) -> Unit,
    onApply: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth().height(450.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .weight(
                    1f
                ).padding(
                    horizontal = 8.dp
                ).clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            LocationPickerView(
                initialLocation = tempLocation,
                markerColor = selectedColor,
                onLocationPicked = onLocationPicked
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onApply,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            HaveText(stringResource(R.string.apply_button))
        }
    }
}

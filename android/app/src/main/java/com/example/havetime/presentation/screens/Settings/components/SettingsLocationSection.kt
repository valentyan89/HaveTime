package com.example.havetime.presentation.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.R
import com.example.havetime.domain.model.settingsPrefs.Location
import com.example.havetime.presentation.common.utils.HaveText

@Composable
internal fun SettingsLocationSection(selectedLocation: Location, onLocationChanged: (Location) -> Unit) {
    HaveText(
        text = stringResource(R.string.settings_location),
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        style = TextStyle(fontStyle = FontStyle.Normal),
        modifier =
        Modifier.padding(
            horizontal = 16.dp,
            vertical = 12.dp
        )
    )

    Column(
        modifier = Modifier.selectableGroup()
    ) {
        Location.entries.forEach { location ->
            SettingsRadioItem(
                title = stringResource(location.toStringRes()),
                selected = selectedLocation == location,
                onClick = {
                    onLocationChanged(location)
                }
            )
        }
    }
}

package com.example.havetime.presentation.screens.settings.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.R
import com.example.havetime.domain.model.settingsPrefs.ColorMode
import com.example.havetime.domain.model.settingsPrefs.Language
import com.example.havetime.domain.model.settingsPrefs.Location
import com.example.havetime.presentation.common.utils.HaveText

@Composable
internal fun SettingsRadioItem(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            ).padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HaveText(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            style = TextStyle(fontStyle = FontStyle.Normal),
            modifier = Modifier.weight(1f)
        )

        RadioButton(
            selected = selected,
            onClick = null
        )
    }
}

@Composable
internal fun SettingsSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch
            ).padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        HaveText(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            style = TextStyle(fontStyle = FontStyle.Normal),
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = null
        )
    }
}

@StringRes
fun Language.toStringRes(): Int = when (this) {
    Language.SYSTEM -> R.string.language_system
    Language.RUSSIAN -> R.string.language_russian
    Language.ENGLISH -> R.string.language_english
}

@StringRes
fun Location.toStringRes(): Int = when (this) {
    Location.COORDS -> R.string.location_coordinates
    Location.GEOCODDING -> R.string.location_geocoding
}

@StringRes
fun ColorMode.toStringRes(): Int = when (this) {
    ColorMode.LIGHT -> R.string.light_theme
    ColorMode.DARK -> R.string.dark_theme
    ColorMode.SYSTEM -> R.string.system_theme
}
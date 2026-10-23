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
import com.example.havetime.domain.model.settingsPrefs.ColorMode
import com.example.havetime.presentation.common.utils.HaveText

@Composable
internal fun SettingsAppearanceSection(
    dynamicColor: Boolean,
    selectedTheme: ColorMode,
    onThemeChanged: (ColorMode) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit
) {
    HaveText(
        modifier =
            Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        text = stringResource(R.string.settings_appearance),
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        style = TextStyle(fontStyle = FontStyle.Normal)
    )

    Column(
        modifier = Modifier.selectableGroup()
    ) {
        ColorMode.entries.forEach { mode ->
            SettingsRadioItem(
                title = stringResource(mode.toStringRes()),
                selected = selectedTheme == mode,
                onClick = {
                    onThemeChanged(mode)
                }
            )
        }
        SettingsSwitchItem(
            title = stringResource(R.string.dynamic_color),
            checked = dynamicColor,
            onCheckedChange = onDynamicColorChanged
        )
    }
}

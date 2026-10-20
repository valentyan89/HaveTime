
package com.example.havetime.presentation.screens.Settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.havetime.R
import com.example.havetime.domain.model.settingsPrefs.AppSettings
import com.example.havetime.domain.model.settingsPrefs.Language
import com.example.havetime.domain.model.settingsPrefs.Location
import com.example.havetime.presentation.common.bottom_bar_tab.GlassyBottomBar
import com.example.havetime.presentation.common.utils.HaveText
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    settings: AppSettings,
    navController: NavController,
    onLanguageChanged: (Language) -> Unit,
    onLocationChanged: (Location) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    val hazeState = remember { HazeState() }

    val navigationBarsPadding =
        WindowInsets.navigationBars
            .asPaddingValues()
            .calculateBottomPadding()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    HaveText(
                        text = stringResource(R.string.settings),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = TextStyle(fontStyle = FontStyle.Normal)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        bottomBar = {
            GlassyBottomBar(
                navController = navController,
                hazeState = hazeState
            )
        }
    ) { paddingValues ->

        Column(
            modifier =
            Modifier
                .haze(
                    hazeState,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    tint = Color.Black.copy(alpha = 0.2f),
                    blurRadius = 30.dp
                ).fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = navigationBarsPadding
                ).verticalScroll(rememberScrollState())
        ) {
            HaveText(
                text = stringResource(R.string.settings_language),
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
                Language.entries.forEach { language ->
                    SettingsRadioItem(
                        title = stringResource(language.toStringRes()),
                        selected = settings.language == language,
                        onClick = {
                            onLanguageChanged(language)
                        }
                    )
                }
            }

            HorizontalDivider()

            SettingsSwitchItem(
                title = stringResource(R.string.dynamic_color),
                checked = settings.dynamicColor,
                onCheckedChange = onDynamicColorChanged
            )

            HorizontalDivider()

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
                        selected = settings.location == location,
                        onClick = {
                            onLocationChanged(location)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRadioItem(title: String, selected: Boolean, onClick: () -> Unit) {
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
private fun SettingsSwitchItem(
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
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
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

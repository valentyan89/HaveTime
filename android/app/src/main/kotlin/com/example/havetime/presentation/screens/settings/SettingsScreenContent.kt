package com.example.havetime.presentation.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.havetime.domain.model.settingsPrefs.AppSettings
import com.example.havetime.domain.model.settingsPrefs.ColorMode
import com.example.havetime.domain.model.settingsPrefs.Language
import com.example.havetime.domain.model.settingsPrefs.Location
import com.example.havetime.presentation.common.navigation.bottom_bar.GlassyBottomBar
import com.example.havetime.presentation.screens.settings.components.SettingsAppearanceSection
import com.example.havetime.presentation.screens.settings.components.SettingsLanguageSection
import com.example.havetime.presentation.screens.settings.components.SettingsLocationSection
import com.example.havetime.presentation.screens.settings.components.SettingsTopBar
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

@Composable
fun SettingsScreenContent(
    settings: AppSettings,
    navController: NavController,
    onLanguageChanged: (Language) -> Unit,
    onLocationChanged: (Location) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
    onColorModeChanged: (ColorMode) -> Unit,
    onBackClick: () -> Unit
) {
    val hazeState = remember { HazeState() }
    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        topBar = { SettingsTopBar(onBackClick) },
        bottomBar = { GlassyBottomBar(navController = navController, hazeState = hazeState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .haze(
                    hazeState,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    tint = Color.Black.copy(alpha = 0.2f),
                    blurRadius = 30.dp
                )
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding(), bottom = navigationBarsPadding)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsLanguageSection(settings.language, onLanguageChanged)
            HorizontalDivider()
            SettingsAppearanceSection(
                settings.dynamicColor,
                settings.colorMode,
                onColorModeChanged,
                onDynamicColorChanged
            )
            HorizontalDivider()
            SettingsLocationSection(settings.location, onLocationChanged)
        }
    }
}

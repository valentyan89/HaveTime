package com.example.havetime.presentation.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel(), onBackClick: () -> Unit = {}) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    SettingsScreenContent(
        settings = settings,
        onLanguageChanged = viewModel::setLanguage,
        onLocationChanged = viewModel::setLocation,
        onColorModeChanged = viewModel::setColorMode,
        onDynamicColorChanged = viewModel::setDynamicColor,
        onBackClick = onBackClick
    )
}

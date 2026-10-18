package com.example.havetime.presentation.screens.Settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    SettingsScreenContent(
        settings = settings,
        navController = navController,
        onLanguageChanged = viewModel::setLanguage,
        onLocationChanged = viewModel::setLocation,
        onDynamicColorChanged = viewModel::setDynamicColor,
        onBackClick = onBackClick
    )
}
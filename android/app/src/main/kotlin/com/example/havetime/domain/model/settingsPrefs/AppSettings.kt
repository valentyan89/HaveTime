package com.example.havetime.domain.model.settingsPrefs

data class AppSettings(
    val language: Language = Language.SYSTEM,
    val location: Location = Location.COORDS,
    val colorMode: ColorMode = ColorMode.SYSTEM,
    val dynamicColor: Boolean = false,
    val notificationsEnabled: Boolean = true
)

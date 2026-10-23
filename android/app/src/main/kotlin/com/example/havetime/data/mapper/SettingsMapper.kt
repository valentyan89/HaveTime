package com.example.havetime.data.mapper

import com.example.havetime.domain.model.settingsPrefs.Language
import com.example.havetime.domain.model.settingsPrefs.Location

fun String?.toLanguage(): Language = Language.entries.firstOrNull {
    it.name == this
} ?: Language.SYSTEM

fun String?.toLocation(): Location = Location.entries.firstOrNull {
    it.name == this
} ?: Location.COORDS

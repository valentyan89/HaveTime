package com.example.havetime.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.havetime.domain.model.settingsPrefs.AppSettings
import com.example.havetime.domain.model.settingsPrefs.ColorMode
import com.example.havetime.domain.model.settingsPrefs.Language
import com.example.havetime.domain.model.settingsPrefs.Location
import com.example.havetime.domain.usecase.settings.GetSettingsUseCase
import com.example.havetime.domain.usecase.settings.SetColorModeUseCase
import com.example.havetime.domain.usecase.settings.SetDynamicColorUseCase
import com.example.havetime.domain.usecase.settings.SetLanguageUseCase
import com.example.havetime.domain.usecase.settings.SetLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val setLanguageUseCase: SetLanguageUseCase,
    private val setLocationUseCase: SetLocationUseCase,
    private val setDynamicColorUseCase: SetDynamicColorUseCase,
    private val setColorModeUseCase: SetColorModeUseCase
) : ViewModel() {
    val settings =
        getSettingsUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AppSettings()
            )

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            setLanguageUseCase(language)
        }
    }

    fun setLocation(location: Location) {
        viewModelScope.launch {
            setLocationUseCase(location)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            setDynamicColorUseCase(enabled)
        }
    }

    fun setColorMode(colorMode: ColorMode) {
        viewModelScope.launch {
            setColorModeUseCase(colorMode)
        }
    }
}

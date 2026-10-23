package com.example.havetime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.havetime.presentation.AppViewModel
import com.example.havetime.presentation.common.localization.AppLocaleProvider
import com.example.havetime.presentation.navigation.CalendarNavHost
import com.example.havetime.ui.theme.HaveTimeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val appViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        splashScreen.setKeepOnScreenCondition {
            appViewModel.settings.value == null
        }

        enableEdgeToEdge()
        setContent {
            val settings by appViewModel.settings.collectAsStateWithLifecycle()

            settings?.let { appSettings ->
                AppLocaleProvider(language = appSettings.language) {
                    HaveTimeTheme(
                        colorTheme = appSettings.colorMode,
                        dynamicColor = appSettings.dynamicColor
                    ) {
                        CalendarNavHost()
                    }
                }
            }
        }
    }
}

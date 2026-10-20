package com.example.havetime.presentation.common.localization

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalResources
import com.example.havetime.domain.model.settingsPrefs.Language
import java.util.Locale

@Composable
fun AppLocaleProvider(language: Language, content: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current
    val resources = LocalResources.current

    val localizedConfiguration =
        remember(configuration, language) {
            Configuration(configuration).apply {
                when (language) {
                    Language.RUSSIAN -> setLocale(Locale.forLanguageTag("ru"))
                    Language.ENGLISH -> setLocale(Locale.forLanguageTag("en"))
                    Language.SYSTEM -> Unit
                }
            }
        }

    val localizedResources =
        remember(resources, localizedConfiguration) {
            android.content.res.Resources(
                resources.assets,
                resources.displayMetrics,
                localizedConfiguration
            )
        }

    CompositionLocalProvider(
        LocalConfiguration provides localizedConfiguration,
        LocalResources provides localizedResources
    ) {
        content()
    }
}

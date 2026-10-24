package com.example.havetime.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {
    @Serializable
    data object Day : Screen()

    @Serializable
    data object Month : Screen()

    @Serializable
    data class Year(val year: Int) : Screen()

    @Serializable
    data object Map : Screen()

    @Serializable
    data object Auth : Screen()

    @Serializable
    data object Settings : Screen()
}

package com.example.havetime.presentation.navigation

import androidx.navigation3.runtime.NavKey

fun MutableList<NavKey>.navigate(screen: Screen) {
    if (lastOrNull() == screen) return
    if (!popTo(screen)) add(screen)
}

fun MutableList<NavKey>.goBack(): Boolean {
    if (size <= 1) return false
    removeAt(lastIndex)
    return true
}

fun MutableList<NavKey>.popTo(screen: Screen): Boolean {
    val index = indexOfLast { it == screen }
    if (index < 0) return false
    while (lastIndex > index) removeAt(lastIndex)
    return true
}

fun MutableList<NavKey>.replaceTop(screen: Screen) {
    if (lastOrNull() == screen) return
    if (isNotEmpty()) removeAt(lastIndex)
    navigate(screen)
}

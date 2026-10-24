package com.example.havetime.presentation.common.navigation.bottom_bar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.havetime.R
import com.example.havetime.presentation.navigation.Screen
import dev.chrisbanes.haze.HazeState

internal class SharedBottomBarState {
    val hazeState = HazeState()
    val addActions = mutableStateMapOf<Screen, () -> Unit>()
}

internal val LocalSharedBottomBar =
    staticCompositionLocalOf<SharedBottomBarState?> {
        null
    }

@Composable
internal fun RegisterBottomBarAddAction(
    screen: Screen,
    onAdd: () -> Unit,
) {
    val host = LocalSharedBottomBar.current ?: return
    val latestOnAdd by rememberUpdatedState(onAdd)

    DisposableEffect(host, screen) {
        val action = {
            latestOnAdd()
        }

        host.addActions[screen] = action

        onDispose {
            if (host.addActions[screen] === action) {
                host.addActions.remove(screen)
            }
        }
    }
}

@Composable
internal fun SharedBottomBarHost(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    val state = remember {
        SharedBottomBarState()
    }

    val showAdd = currentScreen == Screen.Day
            || currentScreen == Screen.Month
            || currentScreen == Screen.Map

    val showBar = currentScreen != Screen.Auth

    CompositionLocalProvider(
        LocalSharedBottomBar provides state
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            content()

            if (showBar) {
                GlassyBottomBar(
                    modifier = Modifier.align(
                        Alignment.BottomCenter
                    ),
                    currentScreen = currentScreen,
                    onNavigate = onNavigate,
                    hazeState = state.hazeState,
                    showTrailingAction = showAdd,
                    trailingAction = { _ ->
                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .clickable(
                                    enabled = showAdd,
                                    onClickLabel = stringResource(
                                        R.string.add_event
                                    ),
                                ) {
                                    state.addActions[currentScreen]?.invoke()
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(
                                    R.string.add_event
                                ),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
                )
            }
        }
    }
}
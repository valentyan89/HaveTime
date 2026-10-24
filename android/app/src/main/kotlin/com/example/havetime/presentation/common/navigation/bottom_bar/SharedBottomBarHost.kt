package com.example.havetime.presentation.common.navigation.bottom_bar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.havetime.R
import com.example.havetime.presentation.common.components.GlassyFloatingActionButton
import com.example.havetime.presentation.navigation.Screen
import dev.chrisbanes.haze.HazeState

internal class SharedBottomBarState {
    val hazeState = HazeState()
    val addActions = mutableStateMapOf<String, () -> Unit>()
}

internal val LocalSharedBottomBar = staticCompositionLocalOf<SharedBottomBarState?> { null }

@Composable
internal fun RegisterBottomBarAddAction(route: String, onAdd: () -> Unit) {
    val host = LocalSharedBottomBar.current ?: return
    val latestOnAdd by rememberUpdatedState(onAdd)
    DisposableEffect(host, route) {
        val action = { latestOnAdd() }
        host.addActions[route] = action
        onDispose {
            if (host.addActions[route] === action) host.addActions.remove(route)
        }
    }
}

@Composable
internal fun SharedBottomBarHost(
    navController: NavController,
    currentRoute: String,
    content: @Composable () -> Unit
) {
    val state = remember { SharedBottomBarState() }
    val showAdd = currentRoute == Screen.Day.route || currentRoute == Screen.Map.route
    val showBar = showAdd || currentRoute == Screen.Settings.route

    CompositionLocalProvider(LocalSharedBottomBar provides state) {
        Box(Modifier.fillMaxSize()) {
            content()
            if (showBar) {
                GlassyBottomBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    navController = navController,
                    hazeState = state.hazeState,
                    showTrailingAction = showAdd,
                    trailingAction = { visibleFraction ->
                        // Haze draws the glass in the source layer, outside the icon's mask.
                        // Trim its registered area too so the background wipes right to left.
                        val hazeShape = remember(visibleFraction) {
                            GenericShape { size, _ ->
                                val circle = Path().apply { addOval(Rect(Offset.Zero, size)) }
                                val visibleArea = Path().apply {
                                    addRect(Rect(0f, 0f, size.width * visibleFraction, size.height))
                                }
                                addPath(Path.combine(PathOperation.Intersect, circle, visibleArea))
                            }
                        }
                        GlassyFloatingActionButton(
                            onClick = { state.addActions[currentRoute]?.invoke() },
                            hazeState = state.hazeState,
                            hazeShape = hazeShape
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_event),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }
        }
    }
}

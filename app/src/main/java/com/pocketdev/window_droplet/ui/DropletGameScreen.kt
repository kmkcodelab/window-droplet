package com.pocketdev.window_droplet.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.pocketdev.window_droplet.model.AppScreen
import com.pocketdev.window_droplet.model.CollectibleDrop
import com.pocketdev.window_droplet.model.Droplet
import com.pocketdev.window_droplet.model.GameStatus
import com.pocketdev.window_droplet.model.GameUiState
import com.pocketdev.window_droplet.model.Obstacle
import com.pocketdev.window_droplet.model.Season
import com.pocketdev.window_droplet.ui.components.DropletCanvas
import com.pocketdev.window_droplet.ui.components.GameHud
import com.pocketdev.window_droplet.ui.components.GameOverDialog
import com.pocketdev.window_droplet.ui.components.SteeringTouchPad
import com.pocketdev.window_droplet.ui.screens.EnvironmentSelectionScreen
import com.pocketdev.window_droplet.ui.screens.HomeScreen
import com.pocketdev.window_droplet.ui.screens.HowToPlayDialog
import com.pocketdev.window_droplet.ui.screens.SettingsDialog
import com.pocketdev.window_droplet.viewmodel.DropletGameViewModel
import kotlinx.coroutines.isActive

@Composable
fun DropletGameScreen(
    viewModel: DropletGameViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val density = LocalDensity.current

    // 60/120 FPS high performance ticker tied to Compose MonotonicFrameClock
    LaunchedEffect(state.status) {
        if (state.status == GameStatus.RUNNING) {
            while (isActive && state.status == GameStatus.RUNNING) {
                withFrameNanos {
                    viewModel.updateGameTick()
                }
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        LaunchedEffect(widthPx, heightPx) {
            if (widthPx > 50f && heightPx > 50f) {
                viewModel.updateScreenDimensions(widthPx, heightPx)
            }
        }

        DropletGameContent(
            state = state,
            onStartGame = { viewModel.startEndlessGame() },
            onOpenEnvironments = { viewModel.navigateTo(AppScreen.ENVIRONMENTS) },
            onOpenGuide = { viewModel.navigateTo(AppScreen.HOW_TO_PLAY) },
            onOpenSettings = { viewModel.navigateTo(AppScreen.SETTINGS) },
            onSelectSeason = { viewModel.changeSeason(it) },
            onBackToHome = { viewModel.returnToHome() },
            onRestart = { viewModel.restartGame() },
            onToggleSound = { viewModel.toggleSound() },
            onToggleHaptic = { viewModel.toggleHaptic() },
            onDirectTouchX = { viewModel.onDirectTouchX(it) },
            onSteer = { dx, dy -> viewModel.onSteerInput(dx, dy) },
            onTouchReleased = { viewModel.onTouchReleased() }
        )
    }
}

@Composable
fun DropletGameContent(
    state: GameUiState,
    onStartGame: () -> Unit,
    onOpenEnvironments: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenSettings: () -> Unit,
    onSelectSeason: (Season) -> Unit,
    onBackToHome: () -> Unit,
    onRestart: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptic: () -> Unit,
    onDirectTouchX: (Float) -> Unit,
    onSteer: (Float, Float) -> Unit,
    onTouchReleased: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (state.activeScreen) {
            AppScreen.HOME -> {
                HomeScreen(
                    state = state,
                    onStartGame = onStartGame,
                    onOpenEnvironments = onOpenEnvironments,
                    onOpenGuide = onOpenGuide,
                    onOpenSettings = onOpenSettings
                )
            }

            AppScreen.ENVIRONMENTS -> {
                EnvironmentSelectionScreen(
                    currentSeason = state.currentSeason,
                    onSelectSeason = onSelectSeason,
                    onBack = onBackToHome
                )
            }

            AppScreen.HOW_TO_PLAY -> {
                HomeScreen(
                    state = state,
                    onStartGame = onStartGame,
                    onOpenEnvironments = onOpenEnvironments,
                    onOpenGuide = onOpenGuide,
                    onOpenSettings = onOpenSettings
                )
                HowToPlayDialog(onDismiss = onBackToHome)
            }

            AppScreen.SETTINGS -> {
                HomeScreen(
                    state = state,
                    onStartGame = onStartGame,
                    onOpenEnvironments = onOpenEnvironments,
                    onOpenGuide = onOpenGuide,
                    onOpenSettings = onOpenSettings
                )
                SettingsDialog(
                    state = state,
                    onToggleSound = onToggleSound,
                    onToggleHaptic = onToggleHaptic,
                    onDismiss = onBackToHome
                )
            }

            AppScreen.GAME -> {
                // Layer 1: Window Canvas Rendering
                DropletCanvas(state = state)

                // Layer 2: In-Game HUD
                GameHud(state = state)

                // Layer 3: Touch Steering Pad (Bottom 65% of screen, zero finger occlusion)
                if (state.status == GameStatus.RUNNING) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.65f)
                            .align(Alignment.BottomCenter)
                    ) {
                        SteeringTouchPad(
                            onDirectTouchX = onDirectTouchX,
                            onSteer = onSteer,
                            onTouchReleased = onTouchReleased,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Layer 4: Game Over Dialog
                if (state.status == GameStatus.GAME_OVER) {
                    GameOverDialog(
                        state = state,
                        onRestart = onRestart,
                        onHome = onBackToHome
                    )
                }
            }
        }
    }
}

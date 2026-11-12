package com.pocketdev.window_droplet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pocketdev.window_droplet.audio.SoundManager
import com.pocketdev.window_droplet.engine.DropletPhysicsEngine
import com.pocketdev.window_droplet.model.AppScreen
import com.pocketdev.window_droplet.model.Droplet
import com.pocketdev.window_droplet.model.GameStatus
import com.pocketdev.window_droplet.model.GameUiState
import com.pocketdev.window_droplet.model.Season
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DropletGameViewModel(application: Application) : AndroidViewModel(application) {

    private val soundManager = SoundManager.getInstance(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var screenWidth = 720f
    private var screenHeight = 1280f

    @Volatile
    private var directTargetTouchX: Float? = null

    @Volatile
    private var swipeDeltaX = 0f

    @Volatile
    private var swipeDeltaY = 0f

    fun updateScreenDimensions(width: Float, height: Float) {
        if (width > 50f && height > 50f) {
            screenWidth = width
            screenHeight = height
            if (_uiState.value.status == GameStatus.IDLE) {
                _uiState.update { current ->
                    current.copy(
                        playerDroplet = current.playerDroplet.copy(
                            x = width / 2f,
                            y = height * 0.28f
                        )
                    )
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(activeScreen = screen) }
    }

    fun startEndlessGame() {
        directTargetTouchX = null
        swipeDeltaX = 0f
        swipeDeltaY = 0f

        _uiState.update { current ->
            current.copy(
                activeScreen = AppScreen.GAME,
                status = GameStatus.RUNNING,
                playerDroplet = Droplet(
                    x = screenWidth / 2f,
                    y = screenHeight * 0.28f
                ),
                collectibles = emptyList(),
                obstacles = emptyList(),
                distanceMeters = 0f,
                currentSection = 1,
                dropsCollected = 0,
                score = 0,
                viewportY = 0f,
                gameOverReason = null,
                showMilestoneNotice = false
            )
        }
    }

    fun restartGame() {
        startEndlessGame()
    }

    fun returnToHome() {
        _uiState.update {
            it.copy(
                activeScreen = AppScreen.HOME,
                status = GameStatus.IDLE
            )
        }
    }

    fun changeSeason(season: Season) {
        _uiState.update { it.copy(currentSeason = season) }
    }

    fun toggleSound() {
        val current = _uiState.value.isSoundEnabled
        val newState = !current
        soundManager.isSoundEnabled = newState
        _uiState.update { it.copy(isSoundEnabled = newState) }
    }

    fun toggleHaptic() {
        _uiState.update { it.copy(isHapticEnabled = !it.isHapticEnabled) }
    }

    // Direct finger tracking for effortless, fatigue-free movement
    fun onDirectTouchX(x: Float) {
        directTargetTouchX = x
    }

    fun onSteerInput(deltaX: Float, deltaY: Float) {
        swipeDeltaX = deltaX
        swipeDeltaY = if (deltaY > 0f) deltaY else 0f
    }

    fun onTouchReleased() {
        directTargetTouchX = null
        swipeDeltaX = 0f
        swipeDeltaY = 0f
    }

    // High performance frame ticker
    fun updateGameTick() {
        if (_uiState.value.status != GameStatus.RUNNING) return

        val result = DropletPhysicsEngine.updatePhysics(
            currentState = _uiState.value,
            steerDeltaX = swipeDeltaX,
            steerDeltaY = swipeDeltaY,
            directTargetX = directTargetTouchX,
            screenWidth = screenWidth,
            screenHeight = screenHeight
        )

        _uiState.value = result.updatedState

        // Decay swipe impulses
        swipeDeltaX *= 0.5f
        swipeDeltaY *= 0.5f

        // Sound triggers
        if (_uiState.value.isSoundEnabled) {
            if (result.collectedSoundTriggered) {
                soundManager.playWaterMerge()
            }
            if (result.hitSoundTriggered) {
                soundManager.playObstacleHit()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}

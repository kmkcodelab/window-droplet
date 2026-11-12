package com.pocketdev.window_droplet.model

enum class AppScreen {
    HOME,
    GAME,
    ENVIRONMENTS,
    HOW_TO_PLAY,
    SETTINGS
}

enum class GameStatus {
    IDLE,
    RUNNING,
    GAME_OVER,
    SECTION_CLEAR
}

data class GameUiState(
    val activeScreen: AppScreen = AppScreen.HOME,
    val status: GameStatus = GameStatus.IDLE,
    val playerDroplet: Droplet = Droplet(x = 360f, y = 250f),
    val collectibles: List<CollectibleDrop> = emptyList(),
    val obstacles: List<Obstacle> = emptyList(),
    val distanceMeters: Float = 0f,
    val currentSection: Int = 1,
    val dropsCollected: Int = 0,
    val score: Int = 0,
    val highScore: Int = 0,
    val currentSeason: Season = Season.MONSOON_CITY,
    val viewportY: Float = 0f,
    val gameOverReason: String? = null,
    val showMilestoneNotice: Boolean = false,
    val milestoneTitle: String? = null,
    val isSoundEnabled: Boolean = true,
    val isHapticEnabled: Boolean = true
) {
    val waterPercentage: Float
        get() {
            val ratio = (playerDroplet.radius - Droplet.MIN_RADIUS) / (Droplet.MAX_RADIUS - Droplet.MIN_RADIUS)
            return (ratio * 100f).coerceIn(0f, 100f)
        }
}

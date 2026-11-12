package com.pocketdev.window_droplet.engine

import com.pocketdev.window_droplet.model.CollectibleDrop
import com.pocketdev.window_droplet.model.Droplet
import com.pocketdev.window_droplet.model.GameStatus
import com.pocketdev.window_droplet.model.GameUiState
import com.pocketdev.window_droplet.model.Obstacle
import com.pocketdev.window_droplet.model.Season
import com.pocketdev.window_droplet.model.TrailPoint
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sqrt
import kotlin.random.Random

object DropletPhysicsEngine {

    private const val BASE_DOWNWARD_SPEED = 4.2f
    private const val MIN_DOWNWARD_SPEED = 3.0f

    data class PhysicsStepResult(
        val updatedState: GameUiState,
        val collectedSoundTriggered: Boolean = false,
        val hitSoundTriggered: Boolean = false
    )

    fun updatePhysics(
        currentState: GameUiState,
        steerDeltaX: Float,
        steerDeltaY: Float,
        directTargetX: Float?,
        screenWidth: Float,
        screenHeight: Float
    ): PhysicsStepResult {
        if (currentState.status != GameStatus.RUNNING) {
            return PhysicsStepResult(currentState)
        }

        val player = currentState.playerDroplet
        val season = currentState.currentSeason

        // 1. Vertical Acceleration: Downward gravity + user swipe down boost + mass gravity
        val downwardBoost = if (steerDeltaY > 0f) (steerDeltaY * 0.25f).coerceAtMost(8.0f) else 0f
        val massSpeedRatio = ((player.radius - Droplet.MIN_RADIUS) / Droplet.INITIAL_RADIUS).coerceAtLeast(0f)
        val targetVy = (BASE_DOWNWARD_SPEED + (massSpeedRatio * 3.2f) + downwardBoost).coerceAtLeast(MIN_DOWNWARD_SPEED)
        val vy = player.vy * 0.82f + targetVy * 0.18f

        // 2. Lateral (X) Movement: Agile direct finger tracking + responsive swipe boost
        val friction = season.slideFriction
        val wind = season.lateralWindForce

        var targetVx: Float
        if (directTargetX != null) {
            // Direct finger tracking: Droplet swiftly glides to finger's horizontal location
            val diffX = directTargetX - player.x
            targetVx = (diffX * 0.22f).coerceIn(-24f, 24f)
        } else {
            // Agile swipe delta: Amplified 2.8x so short thumb flicks move effortlessly across screen
            targetVx = (steerDeltaX * 2.8f).coerceIn(-28f, 28f)
        }

        // Apply lateral wind push (e.g. on vehicle windshield)
        targetVx += wind * 3.5f

        val inertia = (0.65f + (player.mass - 1.0f) * 0.03f).coerceIn(0.60f, 0.78f)
        val vx = (player.vx * inertia + targetVx * (1.0f - inertia)) * friction

        // 3. Screen Boundary Clamping
        val minX = player.radius + 16f
        val maxX = (screenWidth - player.radius - 16f).coerceAtLeast(minX)
        val newX = (player.x + vx).coerceIn(minX, maxX)
        val newY = player.y + vy

        // 4. Liquid Surface Tension: Stretch & Squash deformation
        // Fast vertical fall elongates droplet; lateral slide squashes it horizontally
        val speedMagnitude = hypot(vx, vy)
        val stretchY = (1.0f + (vy / 18f) - (abs(vx) / 35f)).coerceIn(0.85f, 1.35f)
        val stretchX = (1.0f / sqrt(stretchY)).coerceIn(0.75f, 1.25f)

        // 5. Viewport / Camera follow (keep droplet at top 28% of screen)
        val targetDropletScreenY = (screenHeight * 0.28f).coerceAtLeast(100f)
        val newViewportY = newY - targetDropletScreenY

        // 6. Water Trail: Leaves fading moisture droplets behind
        val updatedTrail = mutableListOf<TrailPoint>()
        for (tp in player.trail) {
            val nextAlpha = tp.alpha - 0.035f
            if (nextAlpha > 0.04f) {
                updatedTrail.add(tp.copy(alpha = nextAlpha))
            }
        }
        if (updatedTrail.isEmpty() || hypot(newX - updatedTrail.last().x, newY - updatedTrail.last().y) > 16f) {
            updatedTrail.add(
                TrailPoint(
                    x = newX,
                    y = newY,
                    radius = player.radius * 0.42f,
                    alpha = 0.55f
                )
            )
            if (updatedTrail.size > 22) {
                updatedTrail.removeAt(0)
            }
        }

        var currentRadius = player.radius
        var currentMass = player.mass
        var scoreGained = 0
        var dropsCollectedCount = currentState.dropsCollected
        var gameOverReason: String? = null
        var isGameOver = false
        var soundMergeTriggered = false
        var soundHitTriggered = false

        // 7. Check Collisions with Collectible Droplets (Merging)
        val remainingCollectibles = mutableListOf<CollectibleDrop>()
        for (drop in currentState.collectibles) {
            val dist = hypot(newX - drop.x, newY - drop.y)
            if (!drop.isCollected && dist < (currentRadius + drop.radius)) {
                // Merged into larger droplet!
                currentMass += drop.massValue
                currentRadius = (currentRadius + drop.radius * 0.38f).coerceAtMost(Droplet.MAX_RADIUS)
                scoreGained += (drop.radius * 3.5f).toInt() + 15
                dropsCollectedCount += 1
                soundMergeTriggered = true
            } else {
                if (drop.y >= newViewportY - 120f) {
                    remainingCollectibles.add(drop)
                }
            }
        }

        // 8. Check Collisions with Obstacles
        val remainingObstacles = mutableListOf<Obstacle>()
        for (obs in currentState.obstacles) {
            val dist = hypot(newX - obs.x, newY - obs.y)
            val combinedRadius = currentRadius + obs.radius

            if (dist < combinedRadius) {
                soundHitTriggered = true
                when (obs) {
                    is Obstacle.DustPatch -> {
                        // Dust absorbs water gradually
                        currentRadius -= obs.depletionAmount * 1.2f
                        currentMass = (currentMass - 0.03f).coerceAtLeast(0.3f)
                        if (currentRadius <= Droplet.MIN_RADIUS) {
                            isGameOver = true
                            gameOverReason = "ধুলোবালির দাগে শুকিয়ে ফোঁটা সম্পূর্ণ নিঃশেষ হয়ে গেছে!"
                        }
                    }
                    is Obstacle.GlassCrack -> {
                        // Crack shatters droplet
                        isGameOver = true
                        gameOverReason = "কাচের গভীর ফাটলে লেগে পানির ফোঁটাটি ভেঙে গেছে!"
                    }
                    is Obstacle.GreaseSpot -> {
                        // Grease repels and strips water
                        currentRadius -= 0.6f
                        if (currentRadius <= Droplet.MIN_RADIUS) {
                            isGameOver = true
                            gameOverReason = "তৈলাক্ত দাগের বাষ্পে ফোঁটা বিলীন হয়ে গেছে!"
                        }
                    }
                }
                if (obs.y >= newViewportY - 120f && !isGameOver) {
                    remainingObstacles.add(obs)
                }
            } else {
                if (obs.y >= newViewportY - 120f) {
                    remainingObstacles.add(obs)
                }
            }
        }

        // 9. Procedural Horizon Generation
        val spawnHorizon = newViewportY + screenHeight + 350f
        val highestSpawnedY = (remainingCollectibles.map { it.y } + remainingObstacles.map { it.y }).maxOrNull() ?: newY

        if (highestSpawnedY < spawnHorizon) {
            generateNextSectionRows(
                startY = highestSpawnedY + 70f,
                endY = spawnHorizon,
                screenWidth = screenWidth,
                season = season,
                collectibles = remainingCollectibles,
                obstacles = remainingObstacles
            )
        }

        // 10. Endless Distance & Dynamic Environment Transitions
        val distanceMeters = (newY / 60f).coerceAtLeast(0f)
        val newSection = 1 + (distanceMeters / 120f).toInt()
        val sectionChanged = newSection > currentState.currentSection

        // Dynamic Season Transition every section in endless mode
        val seasonList = Season.values()
        val activeSeason = if (sectionChanged) {
            val nextIndex = (newSection - 1) % seasonList.size
            seasonList[nextIndex]
        } else {
            currentState.currentSeason
        }

        val milestoneMsg = if (sectionChanged) {
            "নতুন পরিবেশ: ${activeSeason.displayName} (${activeSeason.locationName})"
        } else {
            currentState.milestoneTitle
        }

        val finalStatus = if (isGameOver) GameStatus.GAME_OVER else currentState.status
        val updatedScore = currentState.score + scoreGained + (vy * 0.06f).toInt()
        val newHighScore = maxOf(currentState.highScore, updatedScore)

        val updatedState = currentState.copy(
            status = finalStatus,
            playerDroplet = player.copy(
                x = newX,
                y = newY,
                radius = currentRadius,
                mass = currentMass,
                vx = vx,
                vy = vy,
                stretchX = stretchX,
                stretchY = stretchY,
                trail = updatedTrail
            ),
            collectibles = remainingCollectibles,
            obstacles = remainingObstacles,
            viewportY = newViewportY,
            distanceMeters = distanceMeters,
            currentSection = newSection,
            currentSeason = activeSeason,
            dropsCollected = dropsCollectedCount,
            score = updatedScore,
            highScore = newHighScore,
            gameOverReason = gameOverReason ?: currentState.gameOverReason,
            showMilestoneNotice = sectionChanged,
            milestoneTitle = milestoneMsg
        )

        return PhysicsStepResult(
            updatedState = updatedState,
            collectedSoundTriggered = soundMergeTriggered,
            hitSoundTriggered = soundHitTriggered
        )
    }

    private fun generateNextSectionRows(
        startY: Float,
        endY: Float,
        screenWidth: Float,
        season: Season,
        collectibles: MutableList<CollectibleDrop>,
        obstacles: MutableList<Obstacle>
    ) {
        var currentY = startY
        val random = Random.Default
        val safeWidth = screenWidth.coerceAtLeast(320f)

        while (currentY < endY) {
            currentY += random.nextInt(90, 140)

            // Safe passage gap
            val gapCenterX = random.nextFloat() * (safeWidth - 160f).coerceAtLeast(20f) + 80f
            val gapRadius = 70f

            // 1. Spawning Collectibles
            val dropChance = (season.rainDropDensity * 0.75f).coerceIn(0.5f, 0.92f)
            if (random.nextFloat() < dropChance) {
                val dropX = random.nextFloat() * (safeWidth - 70f).coerceAtLeast(20f) + 35f
                if (abs(dropX - gapCenterX) > gapRadius * 0.5f) {
                    collectibles.add(
                        CollectibleDrop(
                            id = System.nanoTime() + random.nextLong(1000),
                            x = dropX,
                            y = currentY,
                            radius = random.nextFloat() * 11f + 11f,
                            massValue = random.nextFloat() * 0.28f + 0.16f
                        )
                    )
                }
            }

            // 2. Spawning Obstacles
            val obstacleChance = when (season) {
                Season.MONSOON_CITY -> 0.38f
                Season.VEHICLE_WINDSHIELD -> 0.45f
                Season.WINTER_FROST -> 0.42f
                Season.VILLAGE_RAIN -> 0.35f
            }

            if (random.nextFloat() < obstacleChance) {
                val obsX = random.nextFloat() * (safeWidth - 90f).coerceAtLeast(20f) + 45f
                if (abs(obsX - gapCenterX) > gapRadius) {
                    val obsTypeRoll = random.nextFloat()
                    val obsId = System.nanoTime() + random.nextLong(1000, 2000)

                    val newObstacle: Obstacle = if (obsTypeRoll < 0.60f) {
                        Obstacle.DustPatch(
                            id = obsId,
                            x = obsX,
                            y = currentY + random.nextInt(-20, 20),
                            radius = random.nextFloat() * 10f + 22f
                        )
                    } else if (obsTypeRoll < 0.85f) {
                        Obstacle.GlassCrack(
                            id = obsId,
                            x = obsX,
                            y = currentY + random.nextInt(-25, 25),
                            radius = random.nextFloat() * 12f + 28f,
                            length = random.nextFloat() * 30f + 55f,
                            angleDeg = random.nextFloat() * 60f + 20f
                        )
                    } else {
                        Obstacle.GreaseSpot(
                            id = obsId,
                            x = obsX,
                            y = currentY + random.nextInt(-20, 20),
                            radius = random.nextFloat() * 10f + 26f
                        )
                    }
                    obstacles.add(newObstacle)
                }
            }
        }
    }
}

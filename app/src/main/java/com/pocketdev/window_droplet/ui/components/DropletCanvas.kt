package com.pocketdev.window_droplet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.pocketdev.window_droplet.model.CollectibleDrop
import com.pocketdev.window_droplet.model.Droplet
import com.pocketdev.window_droplet.model.GameStatus
import com.pocketdev.window_droplet.model.GameUiState
import com.pocketdev.window_droplet.model.Obstacle
import com.pocketdev.window_droplet.model.Season
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DropletCanvas(
    state: GameUiState,
    modifier: Modifier = Modifier
) {
    val season = state.currentSeason
    val viewportY = state.viewportY
    val player = state.playerDroplet

    // Theme palette
    val windowBg = Color(season.windowTintColor)
    val dropColor = Color(season.dropletColor)
    val dropHighlight = Color(season.dropletHighlightColor)

    // Pre-calculated constant colors (Zero allocations in hot path)
    val bokehLight1 = Color(0x1FFFFD700) // Warm streetlight
    val bokehLight2 = Color(0x18FF4081) // Red taillight
    val bokehLight3 = Color(0x1400E5FF) // Cyan neon

    val dustBase = Color(0x8A8D6E63)
    val dustCore = Color(0x665D4037)
    val crackMain = Color(0xE6FFFFFF)
    val crackGlow = Color(0x66B0BEC5)
    val greaseRing = Color(0x599C27B0)
    val greaseCore = Color(0x4000BCD4)
    val rainStreakColor = Color(0x1AFFFFFF)

    Canvas(modifier = modifier.fillMaxSize()) {
        // 1. Window Glass Deep Background
        drawRect(color = windowBg)

        // 2. Bokeh Ambient Lights through rain (simulating blurred city / outdoor lights)
        val bokehYShift = (viewportY * 0.12f) % size.height
        drawCircle(
            color = bokehLight1,
            radius = size.width * 0.28f,
            center = Offset(size.width * 0.20f, (size.height * 0.30f - bokehYShift + size.height) % size.height)
        )
        drawCircle(
            color = bokehLight2,
            radius = size.width * 0.24f,
            center = Offset(size.width * 0.82f, (size.height * 0.65f - bokehYShift + size.height) % size.height)
        )
        drawCircle(
            color = bokehLight3,
            radius = size.width * 0.20f,
            center = Offset(size.width * 0.45f, (size.height * 0.85f - bokehYShift + size.height) % size.height)
        )

        // 3. Background Glass Rain Streaks (Parallax ambient motion)
        val streakSpacing = 80f
        val rawOffset = (viewportY * 0.35f) % streakSpacing
        val streakOffset = (rawOffset + streakSpacing) % streakSpacing
        var streakX = 24f
        while (streakX < size.width) {
            var yPos = streakOffset - 50f
            while (yPos < size.height + 70f) {
                drawLine(
                    color = rainStreakColor,
                    start = Offset(streakX, yPos),
                    end = Offset(streakX + season.lateralWindForce * 12f + 4f, yPos + 32f),
                    strokeWidth = 1.8f,
                    cap = StrokeCap.Round
                )
                yPos += 130f
            }
            streakX += streakSpacing
        }

        // 4. Render Obstacles
        for (obs in state.obstacles) {
            val screenY = obs.y - viewportY
            if (screenY >= -100f && screenY <= size.height + 100f) {
                when (obs) {
                    is Obstacle.DustPatch -> {
                        // Multi-ring organic dust accumulation
                        drawCircle(
                            color = dustBase,
                            radius = obs.radius,
                            center = Offset(obs.x, screenY)
                        )
                        drawCircle(
                            color = dustCore,
                            radius = obs.radius * 0.62f,
                            center = Offset(obs.x - 2f, screenY - 2f)
                        )
                        // Tiny textured speckles
                        drawCircle(
                            color = Color(0x663E2723),
                            radius = obs.radius * 0.25f,
                            center = Offset(obs.x + obs.radius * 0.35f, screenY + obs.radius * 0.25f)
                        )
                    }
                    is Obstacle.GlassCrack -> {
                        val angleRad = Math.toRadians(obs.angleDeg.toDouble())
                        val cosVal = cos(angleRad).toFloat()
                        val sinVal = sin(angleRad).toFloat()
                        val halfLen = obs.length * 0.5f

                        val startX = obs.x - cosVal * halfLen
                        val startY = screenY - sinVal * halfLen
                        val endX = obs.x + cosVal * halfLen
                        val endY = screenY + sinVal * halfLen

                        // Glass crack glow aura
                        drawLine(
                            color = crackGlow,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 5.0f,
                            cap = StrokeCap.Round
                        )
                        // Sharp white fracture
                        drawLine(
                            color = crackMain,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 2.4f,
                            cap = StrokeCap.Round
                        )
                        // Branching micro-fracture
                        drawLine(
                            color = crackMain,
                            start = Offset(obs.x, screenY),
                            end = Offset(obs.x + sinVal * 22f, screenY - cosVal * 22f),
                            strokeWidth = 1.6f,
                            cap = StrokeCap.Round
                        )
                    }
                    is Obstacle.GreaseSpot -> {
                        // Iridescent sheen
                        drawCircle(
                            color = greaseRing,
                            radius = obs.radius,
                            center = Offset(obs.x, screenY),
                            style = Stroke(width = 4f)
                        )
                        drawCircle(
                            color = greaseCore,
                            radius = obs.radius * 0.75f,
                            center = Offset(obs.x, screenY)
                        )
                    }
                }
            }
        }

        // 5. Render Collectible Raindrops (Glistening Dew Drops)
        for (collectible in state.collectibles) {
            val screenY = collectible.y - viewportY
            if (screenY >= -50f && screenY <= size.height + 50f) {
                val center = Offset(collectible.x, screenY)
                val rad = collectible.radius

                // Soft meniscus shadow/glow
                drawCircle(
                    color = dropColor.copy(alpha = 0.22f),
                    radius = rad + 2.5f,
                    center = center
                )
                // Water body
                drawCircle(
                    color = dropColor,
                    radius = rad,
                    center = center
                )
                // Primary specular glare
                drawCircle(
                    color = dropHighlight,
                    radius = rad * 0.32f,
                    center = Offset(collectible.x - rad * 0.30f, screenY - rad * 0.30f)
                )
            }
        }

        // 6. Water Trail behind Player Droplet
        for (tp in player.trail) {
            val screenY = tp.y - viewportY
            if (screenY >= -50f && screenY <= size.height + 50f) {
                drawCircle(
                    color = dropColor.copy(alpha = tp.alpha * 0.45f),
                    radius = tp.radius,
                    center = Offset(tp.x, screenY)
                )
            }
        }

        // 7. Render Player Droplet with Stretch & Surface Specular
        val playerScreenY = player.y - viewportY
        val pCenter = Offset(player.x, playerScreenY)
        val baseR = player.radius
        val sX = player.stretchX
        val sY = player.stretchY

        val dropWidth = baseR * 2f * sX
        val dropHeight = baseR * 2f * sY

        // Soft outer caustic shadow
        drawOval(
            color = dropColor.copy(alpha = 0.30f),
            topLeft = Offset(pCenter.x - (dropWidth + 5f) / 2f, pCenter.y - (dropHeight + 5f) / 2f),
            size = Size(dropWidth + 5f, dropHeight + 5f)
        )

        // Main Water Droplet Body
        drawOval(
            color = dropColor,
            topLeft = Offset(pCenter.x - dropWidth / 2f, pCenter.y - dropHeight / 2f),
            size = Size(dropWidth, dropHeight)
        )

        // Droplet Inner Refraction Rim
        drawOval(
            color = dropHighlight.copy(alpha = 0.40f),
            topLeft = Offset(pCenter.x - (dropWidth * 0.88f) / 2f, pCenter.y - (dropHeight * 0.88f) / 2f),
            size = Size(dropWidth * 0.88f, dropHeight * 0.88f),
            style = Stroke(width = 2.0f)
        )

        // Primary Specular Glare (curved top-left highlight)
        val highlightR = baseR * 0.30f
        drawCircle(
            color = dropHighlight,
            radius = highlightR,
            center = Offset(pCenter.x - (dropWidth * 0.22f), pCenter.y - (dropHeight * 0.22f))
        )

        // Secondary Soft Bottom-Right Light Bounce
        drawCircle(
            color = Color.White.copy(alpha = 0.55f),
            radius = highlightR * 0.45f,
            center = Offset(pCenter.x + (dropWidth * 0.20f), pCenter.y + (dropHeight * 0.20f))
        )
    }
}

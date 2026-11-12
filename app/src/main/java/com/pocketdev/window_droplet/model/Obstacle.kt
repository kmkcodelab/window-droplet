package com.pocketdev.window_droplet.model

sealed interface Obstacle {
    val id: Long
    val x: Float
    val y: Float
    val radius: Float

    // Depleting obstacle: dust / sand patch that absorbs water and shrinks droplet
    data class DustPatch(
        override val id: Long,
        override val x: Float,
        override val y: Float,
        override val radius: Float = 32f,
        val depletionAmount: Float = 0.45f
    ) : Obstacle

    // Fatal / instakill obstacle: deep glass fracture / crack
    data class GlassCrack(
        override val id: Long,
        override val x: Float,
        override val y: Float,
        override val radius: Float = 38f,
        val length: Float = 75f,
        val angleDeg: Float = 35f
    ) : Obstacle

    // Grease spot: causes slipping / momentum drift
    data class GreaseSpot(
        override val id: Long,
        override val x: Float,
        override val y: Float,
        override val radius: Float = 36f
    ) : Obstacle
}

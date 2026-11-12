package com.pocketdev.window_droplet.model

data class TrailPoint(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float
)

data class Droplet(
    val x: Float,
    val y: Float,
    val radius: Float = INITIAL_RADIUS,
    val mass: Float = 1.0f,
    val vx: Float = 0f,
    val vy: Float = 0f,
    val stretchX: Float = 1.0f,
    val stretchY: Float = 1.0f,
    val trail: List<TrailPoint> = emptyList()
) {
    companion object {
        const val INITIAL_RADIUS = 28f
        const val MIN_RADIUS = 10f
        const val MAX_RADIUS = 58f
    }
}

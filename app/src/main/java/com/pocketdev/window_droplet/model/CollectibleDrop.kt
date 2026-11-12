package com.pocketdev.window_droplet.model

data class CollectibleDrop(
    val id: Long,
    val x: Float,
    val y: Float,
    val radius: Float,
    val massValue: Float,
    val isCollected: Boolean = false
)

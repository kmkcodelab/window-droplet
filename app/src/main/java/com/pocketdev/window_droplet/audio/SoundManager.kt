package com.pocketdev.window_droplet.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

class SoundManager private constructor(private val context: Context?) {

    private var soundPool: SoundPool? = null
    private var waterMergeSoundId: Int = 0
    private var obstacleHitSoundId: Int = 0

    var isSoundEnabled: Boolean = true

    init {
        try {
            if (context != null) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                soundPool = SoundPool.Builder()
                    .setMaxStreams(4)
                    .setAudioAttributes(audioAttributes)
                    .build()

                // Load procedural raw sound resources
                context.resources.getIdentifier("water_merge", "raw", context.packageName).let { resId ->
                    if (resId != 0) {
                        waterMergeSoundId = soundPool?.load(context, resId, 1) ?: 0
                    }
                }

                context.resources.getIdentifier("obstacle_hit", "raw", context.packageName).let { resId ->
                    if (resId != 0) {
                        obstacleHitSoundId = soundPool?.load(context, resId, 1) ?: 0
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("SoundManager", "SoundPool initialization skipped: ${e.message}")
        }
    }

    fun playWaterMerge(volume: Float = 0.85f) {
        if (!isSoundEnabled || soundPool == null || waterMergeSoundId == 0) return
        try {
            soundPool?.play(waterMergeSoundId, volume, volume, 1, 0, 1.0f)
        } catch (_: Exception) {}
    }

    fun playObstacleHit(volume: Float = 0.9f) {
        if (!isSoundEnabled || soundPool == null || obstacleHitSoundId == 0) return
        try {
            soundPool?.play(obstacleHitSoundId, volume, volume, 1, 0, 1.0f)
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            soundPool?.release()
            soundPool = null
        } catch (_: Exception) {}
    }

    companion object {
        @Volatile
        private var instance: SoundManager? = null

        fun getInstance(context: Context?): SoundManager {
            return instance ?: synchronized(this) {
                instance ?: SoundManager(context?.applicationContext).also { instance = it }
            }
        }
    }
}

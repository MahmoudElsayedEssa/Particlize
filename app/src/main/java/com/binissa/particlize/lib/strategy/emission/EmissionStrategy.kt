package com.binissa.particlize.lib.strategy.emission

import android.graphics.Bitmap
import kotlin.time.Duration

interface EmissionStrategy {
    fun calculateEmissionPoints(bitmap: Bitmap, density: Int): List<EmissionPoint>
    fun getEmissionProgress(currentTime: Duration, totalDuration: Duration): Float
    fun shouldEmitParticle(point: EmissionPoint, currentTime: Duration, totalDuration: Duration): Boolean
}

class EmissionPoint(
    val x: Int,
    val y: Int,
    var order: Int = 0,
    var progress: Float = 0f,
    val userData: MutableMap<String, Any>? = mutableMapOf()
) {
    /**
     * Add data to the EmissionPoint's userData map.
     * Useful for storing pixel color, sample coordinates, etc.
     */
    fun withData(key: String, value: Any): EmissionPoint {
        userData?.put(key, value)
        return this
    }
}
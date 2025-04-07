package com.binissa.particlize.lib.core.model

import kotlin.time.Duration


data class Particle(
    var id: Int,
    var initialX: Float,
    var initialY: Float,
    var x: Float,
    var y: Float,
    var rotation: Float = 0f,
    var scale: Float = 1f,
    var alpha: Int = 255,
    var color: Int,
    var radius: Float,
    var lifetime: Duration,
    var elapsedTime: Duration = Duration.ZERO,
    var currentTime: Duration = Duration.ZERO,
    var userData: MutableMap<String, Any> = mutableMapOf()
) {
    /**
     * Current progress (0 to 1) of particle lifetime.
     */
    val progress: Float
        get() {
            if (lifetime <= Duration.ZERO) return 1f
            val progressValue =
                (elapsedTime.inWholeNanoseconds.toFloat() / lifetime.inWholeNanoseconds.toFloat())
            return progressValue.coerceIn(0f, 1f)
        }

    /**
     * Particle is alive if it's within its lifetime and still visible.
     */
    val isAlive: Boolean
        get() = elapsedTime < lifetime && alpha > 0

    /**
     * Update particle time.
     */
    fun updateTime(deltaTime: Duration) {
        elapsedTime += deltaTime
    }

    /**
     * Update position.
     */
    fun withPosition(newX: Float, newY: Float) {
        x = newX
        y = newY
    }

    /**
     * Update appearance properties.
     */
    fun withAppearance(
        newAlpha: Int = alpha, newScale: Float = scale, newRotation: Float = rotation
    ) {
        alpha = newAlpha
        scale = newScale
        rotation = newRotation
    }

    /**
     * Add user data.
     */
    fun withUserData(key: String, value: Any) {
        userData[key] = value
    }

    /**
     * Reset particle for reuse.
     */
    fun reset() {
        elapsedTime = Duration.ZERO
        currentTime = Duration.ZERO
        x = initialX
        y = initialY
        rotation = 0f
        scale = 1f
        userData.clear()
    }

}
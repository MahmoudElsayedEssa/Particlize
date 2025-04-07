package com.binissa.particlize.lib.strategy.appearance

import com.binissa.particlize.lib.core.model.Particle
import kotlin.random.Random
import kotlin.time.Duration

class RotationStrategy(
    private val rotationSpeed: Float = 1.0f,    // Degrees per second
    private val randomDirection: Boolean = true,
    private val rotationAcceleration: Float = 0f // Acceleration multiplier
) : AppearanceStrategy {

    override fun updateAppearance(particle: Particle, deltaTime: Duration) {
        // Get or initialize rotation direction
        if (randomDirection && !particle.userData.containsKey("rotationDir")) {
            val random = Random(particle.id)
            particle.userData["rotationDir"] = if (random.nextBoolean()) 1f else -1f
        }

        val rotationDir = if (randomDirection) {
            particle.userData["rotationDir"] as Float
        } else {
            1f
        }

        // Convert deltaTime to seconds for proper rotation calculation
        val deltaSeconds = deltaTime.inWholeNanoseconds / 1_000_000_000f

        // Calculate base rotation delta (degrees)
        val rotationDelta = rotationSpeed * 360f * deltaSeconds

        // Apply acceleration if needed
        val adjustedDelta = if (rotationAcceleration != 0f) {
            rotationDelta * (1f + rotationAcceleration * particle.progress)
        } else {
            rotationDelta
        }

        // Update rotation (keep in range 0-360 for efficiency)
        particle.rotation = (particle.rotation + (adjustedDelta * rotationDir)) % 360f
    }
}
package com.binissa.particlize.lib.strategy.physics

import android.graphics.Point
import com.binissa.particlize.lib.core.model.Particle
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random
import kotlin.time.Duration

/**
 * Physics strategy that creates swirling, vortex-like movement.
 * Particles rotate around a center point with configurable behavior.
 */
class VortexPhysics(
    private val centerOffsetX: Float = 0f,     // -1f to 1f (percentage of view width)
    private val centerOffsetY: Float = 0f,     // -1f to 1f (percentage of view height)
    private val rotationSpeed: Float = 1.0f,   // Base rotation speed, radians per second
    private val acceleration: Float = 0.5f,    // Rate of rotation speed increase over time
    private val pullStrength: Float = 0.5f,    // How strongly particles are pulled to the center
    private val initialOutwardForce: Float = 0f, // Initial outward force (0-1)
    private val turbulence: Float = 0.2f,      // Random movement amount (0-1)
    private val clockwise: Boolean = true      // Direction of rotation
) : PhysicsStrategy {

    override fun updateParticle(
        particle: Particle, deltaTime: Duration, centerPoint: Point?
    ) {
        if (!particle.isAlive) return

        // Calculate actual center point with offset
        val viewWidth = centerPoint?.x?.times(2) ?: 1000
        val viewHeight = centerPoint?.y?.times(2) ?: 1000

        val actualCenterX = (centerPoint?.x ?: 500) + (viewWidth * centerOffsetX / 2)
        val actualCenterY = (centerPoint?.y ?: 500) + (viewHeight * centerOffsetY / 2)

        // Get current particle position relative to center
        val dx = particle.x - actualCenterX
        val dy = particle.y - actualCenterY

        // Calculate distance and angle from center
        val distance = hypot(dx, dy)
        var angle = atan2(dy, dx)

        // Progress affects some behaviors
        val progress = particle.progress

        // Apply rotation - speed increases with acceleration parameter and time
        val rotationFactor = rotationSpeed * (1.0f + acceleration * progress)
        val rotationAmount = rotationFactor * deltaTime.inWholeMilliseconds / 1000f

        // Apply rotation in correct direction
        angle += if (clockwise) {
            -rotationAmount
        } else {
            rotationAmount
        }

        // Calculate pull effect (stronger at edges, weaker at center)
        // Gives a natural spiral effect
        val centerPull = pullStrength * progress * deltaTime.inWholeMilliseconds / 1000f * 100f
        val newDistance = distance * (1f - centerPull / distance)

        // Add initial outward force that diminishes with time
        val initialForce =
            initialOutwardForce * (1f - progress) * deltaTime.inWholeMilliseconds / 500f * 100f
        val adjustedDistance = newDistance + initialForce

        // Calculate new position based on angle and distance
        var newX = actualCenterX + adjustedDistance * cos(angle)
        var newY = actualCenterY + adjustedDistance * sin(angle)

        // Add turbulence (random movement)
        if (turbulence > 0) {
            // Use particle ID as seed for consistent random movement
            val random = Random(particle.id)
            val turbulenceAmount = turbulence * 10f * deltaTime.inWholeMilliseconds / 1000f

            newX += random.nextFloat() * turbulenceAmount * 2 - turbulenceAmount
            newY += random.nextFloat() * turbulenceAmount * 2 - turbulenceAmount
        }

        // Apply rotation to particle itself
        val newRotation = particle.rotation + rotationAmount * 30f // Convert to degrees

        // Update particle
        particle.updateTime(deltaTime)
        particle.withPosition(newX, newY)
        particle.withAppearance(particle.alpha, particle.scale, newRotation)

    }
}
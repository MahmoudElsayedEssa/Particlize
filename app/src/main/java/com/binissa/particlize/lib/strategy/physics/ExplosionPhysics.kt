package com.binissa.particlize.lib.strategy.physics

import android.graphics.Point
import android.util.Log
import com.binissa.particlize.lib.core.model.Particle
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.time.Duration

/**
 * Physics strategy that simulates an explosion effect.
 * Particles move outward from a center point with initial velocity, subject to decay and optional gravity.
 */
class ExplosionPhysicsStrategy(
    private val initialVelocityMin: Float = 2f, // Minimum initial velocity
    private val initialVelocityMax: Float = 8f, // Maximum initial velocity
    private val velocityDecay: Float = 0.95f,   // Velocity decay factor (0.9-0.99 for realistic slowdown)
    private val gravityY: Float = 0.1f,         // Gravity effect (0 for no gravity)
    private val randomness: Float = 0.5f,       // Random variation in movement (0-1)
    private val explosionCenterXOffset: Float = 0f, // Offset X from center point
    private val explosionCenterYOffset: Float = 0f  // Offset Y from center point
) : PhysicsStrategy {

    // Store particle velocities
    private val particleVelocities = mutableMapOf<Int, Pair<Float, Float>>()

    // Random generator
    private val random = Random(System.currentTimeMillis())

    override fun updateParticle(
        particle: Particle,
        deltaTime: Duration,
        centerPoint: Point?
    ) {
        try {
            // Get or initialize particle velocity
            val velocity = particleVelocities[particle.id] ?: initializeVelocity(particle, centerPoint)

            // Calculate delta seconds for smooth physics regardless of frame rate
            val deltaSeconds = deltaTime.inWholeNanoseconds / 1_000_000_000.0f

            // Apply randomness to each update for more natural movement
            val randomX = (random.nextFloat() * 2 - 1) * randomness
            val randomY = (random.nextFloat() * 2 - 1) * randomness

            // Calculate new position with velocity and randomness
            val newX = particle.x + velocity.first * deltaSeconds + randomX
            var newY = particle.y + velocity.second * deltaSeconds + randomY

            // Apply gravity
            newY += gravityY * particle.progress * deltaSeconds * 60f

            // Update particle position
            particle.withPosition(newX, newY)

            // Apply velocity decay
            val newVelocityX = velocity.first * velocityDecay
            val newVelocityY = velocity.second * velocityDecay

            // Store updated velocity
            particleVelocities[particle.id] = newVelocityX to newVelocityY

            // Add slight rotation based on velocity for visual interest
            val rotation = particle.rotation + (newVelocityX + newVelocityY) * 0.2f * deltaSeconds

            // Update rotation and optionally other properties
            particle.withAppearance(
                newAlpha = particle.alpha,
                newScale = particle.scale,
                newRotation = rotation
            )
        } catch (e: Exception) {
            Log.e("ExplosionPhysics", "Error updating particle: ${e.message}")
        }
    }

    /**
     * Initialize the velocity for a particle based on its position relative to center.
     */
    private fun initializeVelocity(particle: Particle, centerPoint: Point?): Pair<Float, Float> {
        // Calculate explosion center
        val centerX = (centerPoint?.x ?: 0) + explosionCenterXOffset
        val centerY = (centerPoint?.y ?: 0) + explosionCenterYOffset

        // Calculate direction from center to particle
        val dx = particle.initialX - centerX
        val dy = particle.initialY - centerY

        // Handle case where particle is at center
        if (dx.isNearZero() && dy.isNearZero()) {
            // Random angle for particles at center
            val angle = random.nextFloat() * 2 * Math.PI
            val velocityMagnitude = random.nextFloat() * (initialVelocityMax - initialVelocityMin) + initialVelocityMin

            val vx = (cos(angle) * velocityMagnitude).toFloat()
            val vy = (sin(angle) * velocityMagnitude).toFloat()

            return vx to vy
        }

        // Normalize direction
        val distance = sqrt(dx * dx + dy * dy)
        val dirX = dx / distance
        val dirY = dy / distance

        // Calculate velocity based on distance and random factor
        val velocityFactor = random.nextFloat() * (initialVelocityMax - initialVelocityMin) + initialVelocityMin

        // Calculate initial velocity
        val vx = dirX * velocityFactor
        val vy = dirY * velocityFactor

        // Store and return velocity
        val velocity = vx to vy
        particleVelocities[particle.id] = velocity

        return velocity
    }

    /**
     * Helper extension to check if a float is nearly zero
     */
    private fun Float.isNearZero(): Boolean = this > -0.0001f && this < 0.0001f
}
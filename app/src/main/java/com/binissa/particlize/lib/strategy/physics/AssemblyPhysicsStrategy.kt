package com.binissa.particlize.lib.strategy.physics

import android.graphics.Point
import android.util.Log
import com.binissa.particlize.lib.core.model.Particle
import com.binissa.particlize.lib.strategy.emission.AssemblyEmissionStrategy
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration

/**
 * Physics strategy for assembly effects that ensures all movement completes within
 * the specified effect duration.
 */
class AssemblyPhysicsStrategy(
    private val speed: Float = 1.0f,          // Movement speed multiplier (0.5-2.0)
    private val jitter: Float = 0.3f,         // Random movement during travel (0-1)
    private val easingType: EasingType = EasingType.ELASTIC, // How particles arrive
    private val settlementThreshold: Float = 5.0f, // How close to final position to be considered "settled"
    private val staggerReserveFactor: Float = 0.25f // Portion of duration reserved for staggering (0-0.5)
) : PhysicsStrategy {

    enum class EasingType {
        LINEAR,     // Constant speed
        SMOOTH,     // Gradual deceleration
        ELASTIC,    // Slight bounce at the end
        BOUNCE      // Multiple bounces at the end
    }

    // Hold movement data for each particle
    private val particleData = mutableMapOf<Int, MovementData>()

    // Track which particles have settled
    private val settledParticles = mutableSetOf<Int>()

    // Reference to emission strategy
    private var emissionStrategy: AssemblyEmissionStrategy? = null

    // Store effect duration for proper timing calculations
    private var effectDuration: Duration? = null

    // Movement data for each particle
    private data class MovementData(
        val startX: Float,
        val startY: Float,
        val finalX: Float,
        val finalY: Float,
        val arrivalTime: Float,  // When particle should arrive (0-1 proportion of effect duration)
        val randomSeed: Int      // For consistent randomization
    )

    /**
     * Set emission strategy and effect duration (called by ParticleSystem)
     */
    fun setEmissionStrategy(strategy: AssemblyEmissionStrategy) {
        emissionStrategy = strategy
    }

    /**
     * Set the total effect duration to ensure particles arrive within this time
     */
    fun setEffectDuration(duration: Duration) {
        effectDuration = duration
        Log.d("AssemblyPhysics", "Effect duration set: $duration")
    }

    /**
     * Check if a particle has settled at its final position
     */
    fun hasParticleSettled(particleId: Int): Boolean {
        return settledParticles.contains(particleId)
    }

    /**
     * Get the percentage of particles that have settled
     */
    fun getSettlementProgress(totalParticles: Int): Float {
        if (totalParticles == 0) return 0f
        return settledParticles.size.toFloat() / totalParticles
    }

    /**
     * Reset settlement tracking
     */
    fun resetSettlement() {
        settledParticles.clear()
        particleData.clear()
    }

    override fun updateParticle(
        particle: Particle,
        deltaTime: Duration,
        centerPoint: Point?
    ) {
        if (!particle.isAlive) return

        // Get or create movement data for this particle
        val data = particleData.getOrPut(particle.id) {
            createMovementData(particle)
        }

        // Calculate time scale for smooth motion regardless of frame rate
        val timeScale = deltaTime.inWholeMilliseconds / 16.0f // Normalized for 60fps

        // Get the effect duration - default to particle lifetime if not set
        val effectDur = effectDuration ?: particle.lifetime

        // Calculate normalized progress (0-1) based on effect duration, not particle lifetime
        // This ensures all particles complete within the effect duration
        val elapsedTime = particle.elapsedTime

        // Calculate what percentage of the effect has elapsed
        val effectProgress = (elapsedTime.inWholeMilliseconds.toFloat() /
                effectDur.inWholeMilliseconds.toFloat()).coerceIn(0f, 1f)

        // Adjust for the particle's scheduled arrival time
        // This gives us a "local" progress for this specific particle
        val normalizedProgress = (effectProgress / data.arrivalTime).coerceIn(0f, 1f)

        // Apply easing function to get smooth movement
        val easedProgress = applyEasing(normalizedProgress)

        // Interpolate between start and final positions
        val newX = interpolate(data.startX, data.finalX, easedProgress)
        val newY = interpolate(data.startY, data.finalY, easedProgress)

        // Calculate jitter amount (more when starting, less when arriving)
        val jitterAmount = if (easedProgress < 0.9f) {
            // More movement during travel
            jitter * 10f * (1f - easedProgress) * timeScale
        } else {
            // Tiny movement when in place
            jitter * 2f * timeScale
        }

        // Apply jitter with consistent randomization
        val random = Random(data.randomSeed + (elapsedTime.inWholeMilliseconds / 100).toInt())
        val jitterX = (random.nextFloat() * 2f - 1f) * jitterAmount
        val jitterY = (random.nextFloat() * 2f - 1f) * jitterAmount

        // Final position after jitter
        val finalX = newX + jitterX
        val finalY = newY + jitterY

        // Update position
        particle.withPosition(finalX, finalY)

        // Update appearance based on movement and progress
        updateAppearance(particle, easedProgress, timeScale)

        // Check if particle has settled
        if (normalizedProgress > 0.9f) {
            // Calculate distance to final position
            val dx = finalX - data.finalX
            val dy = finalY - data.finalY
            val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            // If close enough to final position, mark as settled
            if (distance <= settlementThreshold) {
                settledParticles.add(particle.id)

                // Store settlement state in particle userData
                particle.withUserData("settled", true)

                // Force position to exact final position once settled
                // This ensures perfect alignment at the end
                if (distance > 0.5f) {
                    particle.withPosition(data.finalX, data.finalY)
                }
            }
        }
    }

    private fun createMovementData(particle: Particle): MovementData {
        // Get final position from userData (set by ParticleSystem)
        val finalX = particle.userData["finalX"] as? Float ?: particle.initialX
        val finalY = particle.userData["finalY"] as? Float ?: particle.initialY

        // Get arrival timing (0-1) from userData - this determines when the particle should arrive
        val arrivalTiming = particle.userData["progress"] as? Float ?: 0.5f

        // Scale arrival time to ensure all particles arrive within effect duration
        // We reserve a portion of the duration (staggerReserveFactor) for staggered arrivals
        // and scale by speed to control overall movement speed

        // Limit how much of the effect duration particles can use - ensures all arrive before end
        val maxArrivalTime = 1.0f - staggerReserveFactor

        // Calculate the scaled arrival time
        // - Later particles arrive closer to maxArrivalTime
        // - Speed factor makes everything faster/slower
        // - Scale all arrival times to fit within available time
        val scaledArrival = (maxArrivalTime * (0.5f + arrivalTiming * 0.5f) / speed).coerceIn(0.1f, maxArrivalTime)

        // Log arrival time for this particle
        if (particle.id % 100 == 0) {
            Log.d("AssemblyPhysics", "Particle ${particle.id} arrival time: ${scaledArrival}, arrival timing: $arrivalTiming")
        }

        return MovementData(
            startX = particle.x,
            startY = particle.y,
            finalX = finalX,
            finalY = finalY,
            arrivalTime = scaledArrival,
            randomSeed = particle.id
        )
    }

    private fun applyEasing(progress: Float): Float {
        return when (easingType) {
            EasingType.LINEAR -> progress

            EasingType.SMOOTH -> {
                // Smooth step function
                progress * progress * (3 - 2 * progress)
            }

            EasingType.ELASTIC -> {
                // Elastic easing (slight overshoot with spring-back)
                if (progress >= 1f) return 1f

                val p = 0.3f
                val s = p / 4f

                if (progress <= 0f) return 0f

                val adjustedProgress = progress - 1f
                1f + Math.pow(2.0, 10.0 * adjustedProgress.toDouble()).toFloat() *
                        Math.sin((adjustedProgress - s) * (2 * Math.PI) / p).toFloat()
            }

            EasingType.BOUNCE -> {
                // Bounce easing
                val n1 = 7.5625f
                val d1 = 2.75f

                when {
                    progress < 1f / d1 -> {
                        n1 * progress * progress
                    }
                    progress < 2f / d1 -> {
                        val adjustedProgress = progress - 1.5f / d1
                        n1 * adjustedProgress * adjustedProgress + 0.75f
                    }
                    progress < 2.5f / d1 -> {
                        val adjustedProgress = progress - 2.25f / d1
                        n1 * adjustedProgress * adjustedProgress + 0.9375f
                    }
                    else -> {
                        val adjustedProgress = progress - 2.625f / d1
                        n1 * adjustedProgress * adjustedProgress + 0.984375f
                    }
                }
            }
        }
    }

    private fun interpolate(start: Float, end: Float, progress: Float): Float {
        return start + (end - start) * progress
    }

    private fun updateAppearance(
        particle: Particle,
        progress: Float,
        timeScale: Float
    ) {
        // Update scale - smaller when far from destination
        val targetScale = if (progress < 0.7f) {
            0.5f + 0.5f * progress // Gradually increase size
        } else {
            1.0f // Full size when close
        }

        // Smooth scale transition
        val newScale = particle.scale * 0.9f + targetScale * 0.1f

        // Rotation based on movement
        val rotationSpeed = 15f * (1f - progress.coerceIn(0f, 0.9f))
        val rotationAmount = rotationSpeed * timeScale
        val newRotation = (particle.rotation + rotationAmount) % 360f

        // Update particle appearance
        particle.withAppearance(
            newAlpha = particle.alpha,
            newScale = newScale,
            newRotation = newRotation
        )
    }
}
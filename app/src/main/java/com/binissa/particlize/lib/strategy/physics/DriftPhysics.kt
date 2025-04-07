package com.binissa.particlize.lib.strategy.physics

import android.graphics.Point
import com.binissa.particlize.lib.core.model.Particle
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.time.Duration

class DriftPhysics(
    // Movement controls
    private val horizontalStrength: Float = 1.0f,   // Base horizontal movement strength
    private val verticalStrength: Float = 1.0f,     // Base vertical movement strength
    private val directionAngle: Float = 0f,         // Direction in degrees (0 = right, 90 = down)
    private val directionVariance: Float = 30f,     // Variation in direction (degrees)

    // Directional bias
    private val horizontalBias: Float = 0.0f,      // -1.0 to 1.0 (negative = left, positive = right)
    private val verticalBias: Float = 0.0f,        // -1.0 to 1.0 (negative = up, positive = down)

    // Physics effects
    private val gravity: Float = 0.0f,             // Downward acceleration
    private val windStrength: Float = 0.0f,        // Horizontal force strength
    private val windDirection: Float = 0f,         // Wind direction in degrees (0 = right, 90 = down)
    private val windGustiness: Float = 0.0f,       // How much wind varies (0-1)

    // Chaos factors
    private val turbulenceStrength: Float = 0.0f,  // Random movement strength (0-1)
    private val turbulenceScale: Float = 1.0f,     // Size of turbulence effect

    // Rotation
    private val rotationSpeed: Float = 0.0f,       // Base rotation speed (degrees per second)
    private val rotationVariance: Float = 0.0f     // Variation in rotation speed (0-1)
) : PhysicsStrategy {

    // Constants for physics scaling
    private companion object {
        const val BASE_SPEED = 50f                // Base pixels per second
        const val GRAVITY_SCALING = 300f          // Gravity acceleration scaling
        const val TURBULENCE_SCALING = 30f        // Turbulence movement scaling
        const val WIND_GUST_PERIOD = 2f           // Wind changes over this period (seconds)
    }

    override fun updateParticle(
        particle: Particle, deltaTime: Duration, centerPoint: Point?
    ) {
        if (!particle.isAlive) return

        // Convert delta time to seconds as a float for physics calculations
        val deltaSeconds = deltaTime.inWholeNanoseconds.toFloat() / 1_000_000_000f

        // Get progress through particle lifetime
        val progress = particle.progress

        // Get stable random source for this particle
        val random = Random(particle.id)
        val particleRandomAngle = random.nextFloat() * 360f

        // ---- Calculate movement direction and speed ----

        // Calculate base direction with variance
        val angleVariance = (random.nextFloat() * 2f - 1f) * directionVariance
        val moveAngle = (directionAngle + angleVariance) * Math.PI.toFloat() / 180f

        // Apply directional biases
        val moveX = cos(moveAngle) * horizontalStrength + horizontalBias
        val moveY = sin(moveAngle) * verticalStrength + verticalBias

        // Scale by elapsed time and base speed
        val dx = moveX * BASE_SPEED * deltaSeconds
        val dy = moveY * BASE_SPEED * deltaSeconds

        // ---- Apply gravity ----

        // Gravity increases with time (quadratic)
        val gravityEffect = if (gravity != 0f) {
            val gravityFactor = progress * progress * gravity
            gravityFactor * GRAVITY_SCALING * deltaSeconds
        } else 0f

        // ---- Apply wind ----

        // Wind can gust (vary over time)
        val windEffect = if (windStrength != 0f) {
            // Create time-varying gust effect using sine function
            val gustFactor = if (windGustiness > 0) {
                val gustPhase =
                    (particle.elapsedTime.inWholeSeconds % WIND_GUST_PERIOD) / WIND_GUST_PERIOD
                1f + windGustiness * sin(gustPhase * 2 * Math.PI.toFloat())
            } else 1f

            // Calculate wind direction vector
            val windRad = windDirection * Math.PI.toFloat() / 180f
            val windX = cos(windRad) * windStrength * gustFactor
            val windY = sin(windRad) * windStrength * gustFactor

            // Scale by time and particle size (larger particles affected more by wind)
            Pair(
                windX * BASE_SPEED * deltaSeconds * particle.scale,
                windY * BASE_SPEED * deltaSeconds * particle.scale
            )
        } else Pair(0f, 0f)

        // ---- Apply turbulence ----

        // Turbulence creates random movement in all directions
        val turbulenceEffect = if (turbulenceStrength > 0) {
            // Use Perlin noise for more natural turbulence
            // But we'll approximate with sin/cos functions for simplicity
            val turbTime = particle.elapsedTime.inWholeMilliseconds / 1000f
            val turbPhase = turbTime * turbulenceScale

            // Create pseudo-random but smooth movement using sine and cosine
            val turbX = sin(turbPhase + particleRandomAngle) * turbulenceStrength
            val turbY = cos(turbPhase + particleRandomAngle * 0.7f) * turbulenceStrength

            Pair(
                turbX * TURBULENCE_SCALING * deltaSeconds, turbY * TURBULENCE_SCALING * deltaSeconds
            )
        } else Pair(0f, 0f)

        // ---- Calculate rotation ----

        // Apply rotation based on configured speed and variance
        val rotationChange = if (rotationSpeed != 0f) {
            val baseRotation = rotationSpeed * deltaSeconds
            val rotationVarianceFactor = 1f + (random.nextFloat() * 2f - 1f) * rotationVariance
            baseRotation * rotationVarianceFactor
        } else 0f

        // ---- Update particle ----

        // Calculate new position combining all effects
        val newX = particle.x + dx + windEffect.first + turbulenceEffect.first
        val newY = particle.y + dy + gravityEffect + windEffect.second + turbulenceEffect.second

        // Fade out alpha based on lifetime progress
        val newAlpha = (particle.alpha * (1.0f - progress)).toInt().coerceIn(0, 255)

        // Scale can decrease slightly over time for a natural look
        val newScale = particle.scale * (1.0f - 0.2f * progress)

        // New rotation combines existing rotation with rotation change
        val newRotation = (particle.rotation + rotationChange) % 360f

        // Update the particle
        particle.updateTime(deltaTime)
        particle.withPosition(newX, newY)
        particle.withAppearance(newAlpha, newScale, newRotation)
    }
}
package com.binissa.particlize.lib.strategy.physics

import android.graphics.Point
import android.util.Log
import com.binissa.particlize.lib.core.model.Particle
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.time.Duration

/**
 * Physics strategy that fires particles in random directions with varying velocities.
 * Provides chaotic, unpredictable movement for effects like fireworks, confetti, or sparks.
 */
class RandomFirePhysicsStrategy(
    private val velocityMin: Float = 1f,       // Minimum velocity
    private val velocityMax: Float = 3f,       // Maximum velocity
    private val accelerationMin: Float = -0.5f, // Minimum acceleration (negative = deceleration)
    private val accelerationMax: Float = 0.5f,  // Maximum acceleration
    private val gravityMin: Float = 0f,         // Minimum gravity effect
    private val gravityMax: Float = -0.5f,       // Maximum gravity effect
    private val turbulence: Float = 0.2f,       // Random directional changes (0-1)
    private val directionChangeInterval: Float = 0.1f // How often direction changes (0-1)
) : PhysicsStrategy {

    // Track particle state
    private class ParticleState(
        var velocityX: Float,
        var velocityY: Float,
        var acceleration: Float,
        var gravity: Float,
        var lastDirectionChangeTime: Float = 0f
    )
    
    private val particleStates = mutableMapOf<Int, ParticleState>()
    private val random = Random(System.currentTimeMillis())
    
    override fun updateParticle(
        particle: Particle,
        deltaTime: Duration,
        centerPoint: Point?
    ) {
        try {
            // Get or initialize particle state
            val state = particleStates[particle.id] ?: initializeParticleState(particle)
            
            // Convert to seconds for more consistent physics across frame rates
            val deltaSeconds = deltaTime.inWholeNanoseconds / 1_000_000_000.0f
            
            // Check if it's time for a direction change
            val progressStep = deltaSeconds / particle.lifetime.inWholeSeconds.toFloat()
            state.lastDirectionChangeTime += progressStep
            
            if (state.lastDirectionChangeTime >= directionChangeInterval) {
                state.lastDirectionChangeTime = 0f
                
                // Apply random direction change based on turbulence
                if (random.nextFloat() < turbulence) {
                    val angle = random.nextFloat() * 2 * Math.PI
                    val turbulenceFactor = turbulence * random.nextFloat() * state.acceleration
                    
                    state.velocityX += (cos(angle) * turbulenceFactor).toFloat()
                    state.velocityY += (sin(angle) * turbulenceFactor).toFloat()
                }
            }
            
            // Apply acceleration to velocity
            val accelerationFactor = state.acceleration * deltaSeconds
            val velocityMagnitude = Math.sqrt((state.velocityX * state.velocityX + state.velocityY * state.velocityY).toDouble()).toFloat()
            
            if (velocityMagnitude > 0) {
                val normalizedVelocityX = state.velocityX / velocityMagnitude
                val normalizedVelocityY = state.velocityY / velocityMagnitude
                
                state.velocityX += normalizedVelocityX * accelerationFactor
                state.velocityY += normalizedVelocityY * accelerationFactor
            }
            
            // Apply gravity
            state.velocityY += state.gravity * deltaSeconds * 60f
            
            // Calculate new position
            val newX = particle.x + state.velocityX * deltaSeconds * 60f
            val newY = particle.y + state.velocityY * deltaSeconds * 60f
            
            // Update particle position
            particle.withPosition(newX, newY)
            
            // Apply rotation based on velocity for visual interest
            val rotationSpeed = (Math.abs(state.velocityX) + Math.abs(state.velocityY)) * 0.2f
            val newRotation = particle.rotation + rotationSpeed * deltaSeconds * 60f
            
            particle.withAppearance(
                newAlpha = particle.alpha,
                newScale = particle.scale,
                newRotation = newRotation
            )
            
            // Store updated state
            particleStates[particle.id] = state
            
        } catch (e: Exception) {
            Log.e("RandomFirePhysics", "Error updating particle: ${e.message}")
        }
    }
    
    /**
     * Initialize a random state for a new particle.
     */
    private fun initializeParticleState(particle: Particle): ParticleState {
        // Generate random angle
        val angle = random.nextFloat() * 2 * Math.PI
        
        // Generate random velocity magnitude
        val velocity = random.nextFloat() * (velocityMax - velocityMin) + velocityMin
        
        // Calculate velocity components
        val velocityX = (cos(angle) * velocity).toFloat()
        val velocityY = (sin(angle) * velocity).toFloat()
        
        // Generate random acceleration
        val acceleration = random.nextFloat() * (accelerationMax - accelerationMin) + accelerationMin
        
        // Generate random gravity
        val gravity = random.nextFloat() * (gravityMax - gravityMin) + gravityMin
        
        // Create and store state
        val state = ParticleState(
            velocityX = velocityX,
            velocityY = velocityY,
            acceleration = acceleration,
            gravity = gravity
        )
        
        particleStates[particle.id] = state
        
        return state
    }
}
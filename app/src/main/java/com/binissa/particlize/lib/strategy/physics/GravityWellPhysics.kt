package com.binissa.particlize.lib.strategy.physics

import android.graphics.Point
import com.binissa.particlize.lib.core.model.Particle
import kotlin.math.hypot
import kotlin.random.Random
import kotlin.time.Duration

/**
 * Physics strategy that creates gravitational forces toward or away from points.
 * Can create black hole effects, attraction/repulsion, or orbital movement.
 */
class GravityWellPhysics(
    private val wells: List<GravityWell> = listOf(GravityWell()),
    private val damping: Float = 0.98f,         // Velocity damping (0-1)
    private val initialVelocity: Float = 0.5f,  // Random initial velocity (0-1)
    private val turbulence: Float = 0.1f        // Random movement (0-1)
) : PhysicsStrategy {

    /**
     * Represents a single gravity well (attractor or repulsor).
     */
    data class GravityWell(
        val offsetX: Float = 0f,        // -1f to 1f (percentage of width from center)
        val offsetY: Float = 0f,        // -1f to 1f (percentage of height from center)
        val strength: Float = 1.0f,     // Force strength
        val isRepulsive: Boolean = false, // True = push away, False = pull in
        val radius: Float = 100f,       // Radius of influence
        val falloff: Falloff = Falloff.INVERSE_SQUARE // How force decreases with distance
    ) {
        enum class Falloff {
            NONE,           // Constant force regardless of distance
            LINEAR,         // Force decreases linearly with distance
            INVERSE_SQUARE, // Force decreases with square of distance (realistic)
            EXPONENTIAL     // Force decreases exponentially (sharper falloff)
        }
    }
    
    // Per-particle velocity tracking
    private val particleVelocities = mutableMapOf<Int, Pair<Float, Float>>()
    
    override fun updateParticle(
        particle: Particle, 
        deltaTime: Duration, 
        centerPoint: Point?
    ) {
        if (!particle.isAlive) return
        
        // Get or initialize velocity
        val velocity = particleVelocities.getOrPut(particle.id) {
            val random = Random(particle.id)
            val angle = random.nextFloat() * 2 * Math.PI.toFloat()
            val speed = initialVelocity * 50f
            
            Pair(
                speed * kotlin.math.cos(angle),
                speed * kotlin.math.sin(angle)
            )
        }
        
        var vx = velocity.first
        var vy = velocity.second
        
        // Calculate actual center with dimensions
        val viewWidth = centerPoint?.x?.times(2) ?: 1000
        val viewHeight = centerPoint?.y?.times(2) ?: 1000
        val centerX = centerPoint?.x?.toFloat() ?: (viewWidth / 2f)
        val centerY = centerPoint?.y?.toFloat() ?: (viewHeight / 2f)
        
        // Apply force from each gravity well
        for (well in wells) {
            // Calculate well center
            val wellX = centerX + (viewWidth * well.offsetX / 2)
            val wellY = centerY + (viewHeight * well.offsetY / 2)
            
            // Calculate distance to well
            val dx = wellX - particle.x
            val dy = wellY - particle.y
            val distance = hypot(dx, dy)
            
            // Skip if too far or too close
            if (distance < 0.1f) continue
            if (distance > well.radius && well.radius > 0) continue
            
            // Calculate force magnitude based on falloff
            val forceMagnitude = when (well.falloff) {
                GravityWell.Falloff.NONE -> well.strength
                GravityWell.Falloff.LINEAR -> well.strength * (1f - distance / well.radius)
                GravityWell.Falloff.INVERSE_SQUARE -> well.strength / (distance * distance + 1f)
                GravityWell.Falloff.EXPONENTIAL -> well.strength * kotlin.math.exp(-distance / (well.radius / 3f))
            }
            
            // Calculate normalized direction
            val dirX = dx / distance
            val dirY = dy / distance
            
            // Apply force (attract or repel)
            val sign = if (well.isRepulsive) -1f else 1f
            val timeScale = deltaTime.inWholeMilliseconds / 1000f
            
            vx += dirX * forceMagnitude * sign * 100f * timeScale
            vy += dirY * forceMagnitude * sign * 100f * timeScale
        }
        
        // Add turbulence
        if (turbulence > 0) {
            val random = Random(particle.id + particle.elapsedTime.inWholeMilliseconds.toInt())
            val turbFactor = turbulence * 10f * deltaTime.inWholeMilliseconds / 1000f
            
            vx += (random.nextFloat() * 2f - 1f) * turbFactor
            vy += (random.nextFloat() * 2f - 1f) * turbFactor
        }
        
        // Apply damping to velocity
        vx *= damping
        vy *= damping
        
        // Update position
        val newX = particle.x + vx * deltaTime.inWholeMilliseconds / 1000f
        val newY = particle.y + vy * deltaTime.inWholeMilliseconds / 1000f
        
        // Store updated velocity
        particleVelocities[particle.id] = Pair(vx, vy)
        
        // Update particle
        particle.updateTime(deltaTime)
        particle.withPosition(newX, newY)
        
        // Update rotation based on velocity for natural movement
        val speed = hypot(vx, vy)
        val newRotation = particle.rotation + (speed * 0.1f) % 360f
        
        particle.withAppearance(particle.alpha, particle.scale, newRotation)
        
    }
}
package com.binissa.particlize.lib.config.presets

import android.graphics.Color
import com.binissa.particlize.lib.config.EffectBuilder
import com.binissa.particlize.lib.core.model.ContentDisappearanceMode
import com.binissa.particlize.lib.core.model.ParticleEffect
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasCircleShape
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasRectangleShape
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasStarShape
import com.binissa.particlize.lib.strategy.appearance.ColorTransformStrategy
import com.binissa.particlize.lib.strategy.appearance.FadeStrategy
import com.binissa.particlize.lib.strategy.appearance.RotationStrategy
import com.binissa.particlize.lib.strategy.appearance.ScaleStrategy
import com.binissa.particlize.lib.strategy.emission.AssemblyEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.DirectionalEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.InstantEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.PatternEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.RadialEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.RandomEmissionStrategy
import com.binissa.particlize.lib.strategy.physics.AssemblyPhysicsStrategy
import com.binissa.particlize.lib.strategy.physics.DriftPhysics
import com.binissa.particlize.lib.strategy.physics.ExplosionPhysicsStrategy
import com.binissa.particlize.lib.strategy.physics.GravityWellPhysics
import com.binissa.particlize.lib.strategy.physics.RandomFirePhysicsStrategy
import com.binissa.particlize.lib.strategy.physics.VortexPhysics
import kotlin.time.Duration.Companion.milliseconds

/**
 * A collection of predefined particle effect presets.
 * These presets combine different strategies to create visually appealing effects.
 */
object ParticlePresets {

    /**
     * Explosion effect that blasts particles outward.
     */

    fun disintegration(
        duration: Int = 1500,
        particleDensity: Int = 4,
        initialVelocity: Float = 5f,
        gravity: Float = 0.15f
    ): ParticleEffect {
        return EffectBuilder()
            .withName("Explosion")
            .withDuration(duration.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            )
            .withPhysicsStrategy(
                ExplosionPhysicsStrategy(
                    initialVelocityMin = initialVelocity * 0.6f,
                    initialVelocityMax = initialVelocity * 1.2f,
                    velocityDecay = 0.97f,
                    gravityY = gravity,
                    randomness = 0.3f
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            )
            .withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.SHRINK,
                    minScale = 0.2f,
                    maxScale = 1.0f
                )
            )
            .withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 180f,
                    randomDirection = true
                )
            )
            .withParticleShape(CanvasCircleShape())
            .withParticleLifetime(
                (duration * 0.6).milliseconds,
                (duration * 0.9).milliseconds
            )
            .withParticleDensity(particleDensity)
            .withMaxEmissionsPerFrame(300)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT)
            .build()
    }


    fun explosion(
        duration: Int = 1500,
        particleDensity: Int = 6,
        initialVelocity: Float = 5f,
        gravity: Float = 0.15f
    ): ParticleEffect {
        return EffectBuilder()
            .withName("Explosion")
            .withDuration(duration.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            )
            .withPhysicsStrategy(
                ExplosionPhysicsStrategy(
                    initialVelocityMin = initialVelocity * 0.6f,
                    initialVelocityMax = initialVelocity * 1.2f,
                    velocityDecay = 0.97f,
                    gravityY = gravity,
                    randomness = 0.3f
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            )
            .withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.SHRINK,
                    minScale = 0.2f,
                    maxScale = 1.0f
                )
            )
            .withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 180f,
                    randomDirection = true
                )
            )
            .withParticleShape(CanvasCircleShape())
            .withParticleLifetime(
                (duration * 0.6).milliseconds,
                (duration * 0.9).milliseconds
            )
            .withParticleDensity(particleDensity)
            .withMaxEmissionsPerFrame(300)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT)
            .build()
    }

    /**
     * Fireworks effect with sparkling particles.
     */
    fun fireworks(
        duration: Int = 2000,
        particleDensity: Int = 5
    ): ParticleEffect {
        return EffectBuilder()
            .withName("Fireworks")
            .withDuration(duration.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.EXPLOSION
                )
            )
            .withPhysicsStrategy(
                RandomFirePhysicsStrategy(
                    velocityMin = 3f,
                    velocityMax = 8f,
                    accelerationMin = -0.2f,
                    accelerationMax = 0.4f,
                    gravityMin = 0.05f,
                    gravityMax = 0.2f,
                    turbulence = 0.6f
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            )
            .withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.SHRINK,
                    minScale = 0.2f,
                    maxScale = 1.2f
                )
            )
            .withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 120f,
                    randomDirection = true
                )
            )
            .withAppearanceStrategy(
                ColorTransformStrategy(
                    targetColor = Color.YELLOW,
                    startDelay = (duration * 0.3).milliseconds,
                    duration = (duration * 0.7).milliseconds
                )
            )
            .withParticleShape(CanvasStarShape())
            .withParticleLifetime(
                (duration * 0.5).milliseconds,
                (duration * 0.8).milliseconds
            )
            .withParticleDensity(particleDensity)
            .withMaxEmissionsPerFrame(250)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT)
            .build()
    }

    /**
     * Vortex effect that swirls particles in a spiral.
     */
    fun vortex(
        duration: Int = 2500,
        particleDensity: Int = 6,
        clockwise: Boolean = true
    ): ParticleEffect {
        return EffectBuilder()
            .withName("Vortex")
            .withDuration(duration.milliseconds)
            .withEmissionStrategy(
                PatternEmissionStrategy(
                    pattern = PatternEmissionStrategy.Pattern.VORTEX,
                    clockwise = clockwise
                )
            )
            .withPhysicsStrategy(
                VortexPhysics(
                    rotationSpeed = 3.0f,
                    acceleration = 0.8f,
                    pullStrength = 0.3f,
                    turbulence = 0.15f,
                    clockwise = clockwise
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            )
            .withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.PULSE,
                    minScale = 0.5f,
                    maxScale = 1.3f
                )
            )
            .withParticleShape(CanvasCircleShape())
            .withParticleLifetime(
                (duration * 0.7).milliseconds,
                (duration * 0.9).milliseconds
            )
            .withParticleDensity(particleDensity)
            .withMaxEmissionsPerFrame(200)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
            .build()
    }

    /**
     * Black hole effect that sucks particles into the center.
     */
    fun blackHole(
        duration: Int = 2000,
        particleDensity: Int = 6
    ): ParticleEffect {
        return EffectBuilder()
            .withName("Black Hole")
            .withDuration(2500.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.EDGE_IN
                )
            )
            .withPhysicsStrategy(
                VortexPhysics(
                    rotationSpeed = 2.0f,
                    pullStrength = 0.5f,
                    turbulence = 0.1f,
                    clockwise = true
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            )
            .withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.SHRINK,
                    minScale = 0.1f,
                    maxScale = 1.0f
                )
            )
            .withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 90f,
                    randomDirection = false,
                    rotationAcceleration = 2.0f
                )
            )
            .withParticleLifetime(1500.milliseconds, 2000.milliseconds)
            .withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
            .build()
    }

    /**
     * Disintegration effect that gradually breaks down the content.
     */
    fun disintegrationText(
        duration: Int = 1500,
        particleDensity: Int = 3
    ): ParticleEffect {
        return EffectBuilder()
            .withName("Disintegration")
            .withDuration(duration.milliseconds)
            .withEmissionStrategy(
                DirectionalEmissionStrategy(DirectionalEmissionStrategy.Direction.LEFT_TO_RIGHT, 50)
            )
            .withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.7f,
                    verticalStrength = 0.7f,
                    directionAngle = -45f, // Up-right drift
                    directionVariance = 30f,
                    gravity = 0.1f,
                    turbulenceStrength = 0.3f
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            )
            .withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.SHRINK,
                    minScale = 0.1f,
                    maxScale = 0.1f,
                    scaleRate = 2f
                )
            )
            .withParticleShape(CanvasCircleShape())
            .withParticleLifetime(
                (duration * 0.6).milliseconds,
                (duration * 0.9).milliseconds
            )
            .withParticleDensity(particleDensity)
//            .withMaxEmissionsPerFrame(250)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
            .build()
    }

    /**
     * Rain effect that makes particles fall down like rain drops.
     */
    fun rain(
        duration: Int = 2000,
        particleDensity: Int = 7
    ): ParticleEffect {
        return EffectBuilder()
            .withName("Rain")
            .withDuration(duration.milliseconds)
            .withEmissionStrategy(
                DirectionalEmissionStrategy(
                    direction = DirectionalEmissionStrategy.Direction.TOP_TO_BOTTOM,
                    emissionDelay = 50.milliseconds,
                    emissionRate = 3
                )
            )
            .withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.3f,
                    verticalStrength = 2.0f,
                    directionAngle = 90f, // Straight down
                    directionVariance = 10f,
                    gravity = 0.3f,
                    windStrength = 0.2f,
                    windDirection = 0f, // Wind from left
                    windGustiness = 0.5f
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            )
            .withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.SHRINK,
                    minScale = 0.5f,
                    maxScale = 1.0f
                )
            )
            .withParticleShape(CanvasCircleShape())
            .withParticleLifetime(
                (duration * 0.5).milliseconds,
                (duration * 0.8).milliseconds
            )
            .withParticleDensity(particleDensity)
            .withMaxEmissionsPerFrame(200)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
            .build()
    }

    /**
     * Shatter effect that breaks the content like glass.
     */
    fun shatter(
        duration: Int = 1500,
        particleDensity: Int = 5
    ): ParticleEffect {
        return EffectBuilder()
            .withName("Shatter")
            .withDuration(duration.milliseconds)
            .withEmissionStrategy(
                PatternEmissionStrategy(
                    pattern = PatternEmissionStrategy.Pattern.SHATTER
                )
            )
            .withPhysicsStrategy(
                ExplosionPhysicsStrategy(
                    initialVelocityMin = 3f,
                    initialVelocityMax = 8f,
                    velocityDecay = 0.96f,
                    gravityY = 0.2f,
                    randomness = 0.2f
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            )
            .withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.SHRINK,
                    minScale = 0.3f,
                    maxScale = 1.0f
                )
            )
            .withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 90f,
                    randomDirection = true
                )
            )
            .withParticleShape(CanvasRectangleShape())
            .withParticleLifetime(
                (duration * 0.5).milliseconds,
                (duration * 0.8).milliseconds
            )
            .withParticleDensity(particleDensity)
            .withMaxEmissionsPerFrame(300)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT)
            .build()
    }

    /**
     * Reassembly effect that makes particles form back into the original content.
     */
    fun reassembly(
        duration: Int = 3000,
        particleDensity: Int = 6
    ): ParticleEffect {
        return EffectBuilder()
            .withName("Reassembly")
            .withDuration(duration.milliseconds)
            .withEmissionStrategy(
                AssemblyEmissionStrategy(
                    startPosition = AssemblyEmissionStrategy.StartPosition.RANDOM_OFFSCREEN,
                    assemblyOrder = AssemblyEmissionStrategy.AssemblyOrder.OUTSIDE_IN,
                    scatterFactor = 2.0f,
                    staggering = 0.3f
                )
            )
            .withPhysicsStrategy(
                AssemblyPhysicsStrategy(
                    speed = 1.2f,
                    jitter = 0.2f,
                    easingType = AssemblyPhysicsStrategy.EasingType.ELASTIC
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_IN)
            )
            .withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.GROW,
                    minScale = 0.3f,
                    maxScale = 1.0f
                )
            )
            .withParticleShape(CanvasCircleShape())
            .withParticleLifetime(
                (duration * 0.8).milliseconds,
                (duration * 1.2).milliseconds
            )
            .withParticleDensity(particleDensity)
            .withMaxEmissionsPerFrame(300)
            .withContentDisappearanceMode(ContentDisappearanceMode.ASSEMBLY)
            .build()
    }

    /**
     * Spiral effect that makes particles move in a spiral pattern.
     */
    fun spiral(
        duration: Int = 2000,
        particleDensity: Int = 5,
        clockwise: Boolean = true
    ): ParticleEffect {
        return EffectBuilder()
            .withName("Spiral")
            .withDuration(duration.milliseconds)
            .withEmissionStrategy(
                AssemblyEmissionStrategy(
                    startPosition = AssemblyEmissionStrategy.StartPosition.SPIRAL_IN,
                    assemblyOrder = AssemblyEmissionStrategy.AssemblyOrder.OUTSIDE_IN,
                    scatterFactor = 1.5f,
                    staggering = 0.2f
                )
            )
            .withPhysicsStrategy(
                VortexPhysics(
                    rotationSpeed = 2.0f,
                    acceleration = 0.5f,
                    initialOutwardForce = 0.2f,
                    turbulence = 0.1f,
                    clockwise = clockwise
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            )
            .withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.SHRINK,
                    minScale = 0.3f,
                    maxScale = 1.0f
                )
            )
            .withParticleShape(CanvasCircleShape())
            .withParticleLifetime(
                (duration * 0.7).milliseconds,
                (duration * 0.9).milliseconds
            )
            .withParticleDensity(particleDensity)
            .withMaxEmissionsPerFrame(250)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
            .build()
    }

    /**
     * Confetti effect for celebrations.
     */
    fun confetti(
        duration: Int = 2500,
        particleDensity: Int = 5
    ): ParticleEffect {
        return EffectBuilder()
            .withName("Confetti")
            .withDuration(duration.milliseconds)
            .withEmissionStrategy(
                InstantEmissionStrategy(
                    burstDurationMs = 200,
                    sortMode = InstantEmissionStrategy.SortMode.RANDOM
                )
            )
            .withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f,
                    verticalStrength = 1.5f,
                    directionAngle = 90f, // Down
                    directionVariance = 45f,
                    gravity = 0.15f,
                    windStrength = 0.3f,
                    windGustiness = 0.7f,
                    turbulenceStrength = 0.3f
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            )
            .withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.PULSE,
                    minScale = 0.7f,
                    maxScale = 1.3f
                )
            )
            .withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 180f,
                    randomDirection = true
                )
            )
            .withParticleShape(CanvasRectangleShape())
            .withParticleLifetime(
                (duration * 0.7).milliseconds,
                (duration * 0.9).milliseconds
            )
            .withParticleDensity(particleDensity)
            .withMaxEmissionsPerFrame(300)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT)
            .build()
    }

    /**
     * Magic sparkle effect that creates twinkling particles.
     */
    fun magicSparkle(
        duration: Int = 2000,
        particleDensity: Int = 6
    ): ParticleEffect {
        return EffectBuilder()
            .withName("Magic Sparkle")
            .withDuration(duration.milliseconds)
            .withEmissionStrategy(
                RandomEmissionStrategy(
                    randomnessMode = RandomEmissionStrategy.RandomnessMode.POPCORN,
                    groupSize = 10
                )
            )
            .withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f,
                    verticalStrength = 0.5f,
                    directionAngle = -60f, // Up and right
                    directionVariance = 60f,
                    gravity = -0.05f, // Slight upward drift
                    turbulenceStrength = 0.2f
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.PULSE)
            )
            .withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.PULSE,
                    minScale = 0.5f,
                    maxScale = 1.5f
                )
            )
            .withAppearanceStrategy(
                ColorTransformStrategy(
                    targetColor = Color.rgb(255, 215, 0), // Gold
                    startDelay = (duration * 0.2).milliseconds,
                    duration = (duration * 0.6).milliseconds
                )
            )
            .withParticleShape(CanvasStarShape())
            .withParticleLifetime(
                (duration * 0.6).milliseconds,
                (duration * 0.9).milliseconds
            )
            .withParticleDensity(particleDensity)
            .withMaxEmissionsPerFrame(250)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
            .build()
    }


    // Add these new methods for the example app

    /**
     * Creates a directional assembly effect that assembles content from a specific direction.
     * Perfect for text that appears to be written or drawn in.
     */
    fun directionalAssembly(direction: String = "left-to-right"): ParticleEffect {
        val emissionDirection = when (direction.lowercase()) {
            "right-to-left" -> AssemblyEmissionStrategy.StartPosition.SPIRAL_IN to
                    AssemblyEmissionStrategy.AssemblyOrder.RIGHT_TO_LEFT

            "top-to-bottom" -> AssemblyEmissionStrategy.StartPosition.FROM_TOP to
                    AssemblyEmissionStrategy.AssemblyOrder.TOP_TO_BOTTOM

            "bottom-to-top" -> AssemblyEmissionStrategy.StartPosition.FROM_BOTTOM to
                    AssemblyEmissionStrategy.AssemblyOrder.BOTTOM_TO_TOP

            else -> AssemblyEmissionStrategy.StartPosition.SPIRAL_IN to
                    AssemblyEmissionStrategy.AssemblyOrder.LEFT_TO_RIGHT
        }

        return EffectBuilder()
            .withName("Directional Assembly")
            .withDuration(1800.milliseconds)
            .withEmissionStrategy(
                AssemblyEmissionStrategy(
                    startPosition = emissionDirection.first,
                    assemblyOrder = emissionDirection.second,
                    scatterFactor = 1.5f,
                    staggering = 0.3f
                )
            )
            .withPhysicsStrategy(
                AssemblyPhysicsStrategy(
                    speed = 1.2f,
                    jitter = 0.1f,
                    easingType = AssemblyPhysicsStrategy.EasingType.SMOOTH
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_IN, fadeDuration = 200.milliseconds)
            )
            .withParticleLifetime(1800.milliseconds, 2200.milliseconds)
            .withParticleDensity(2)
            .withMaxEmissionsPerFrame(1000)
            .withContentDisappearanceMode(ContentDisappearanceMode.ASSEMBLY)
            .build()
    }

    /**
     * Creates a directional disintegration effect.
     * Content breaks apart and flows in the specified direction.
     */
    fun directionalDisintegration(direction: String = "left-to-right"): ParticleEffect {
        val emissionDirection = when (direction.lowercase()) {
            "right-to-left" -> DirectionalEmissionStrategy.Direction.RIGHT_TO_LEFT
            "top-to-bottom" -> DirectionalEmissionStrategy.Direction.TOP_TO_BOTTOM
            "bottom-to-top" -> DirectionalEmissionStrategy.Direction.BOTTOM_TO_TOP
            else -> DirectionalEmissionStrategy.Direction.LEFT_TO_RIGHT
        }

        return EffectBuilder()
            .withName("Directional Disintegration")
            .withDuration(1500.milliseconds)
            .withEmissionStrategy(
                DirectionalEmissionStrategy(
                    direction = emissionDirection,
                    emissionRate = 3, 50
                )
            )
            .withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = when (emissionDirection) {
                        DirectionalEmissionStrategy.Direction.LEFT_TO_RIGHT,
                        DirectionalEmissionStrategy.Direction.RIGHT_TO_LEFT -> 0.8f

                        else -> 0.3f
                    },
                    verticalStrength = when (emissionDirection) {
                        DirectionalEmissionStrategy.Direction.TOP_TO_BOTTOM,
                        DirectionalEmissionStrategy.Direction.BOTTOM_TO_TOP -> 0.8f

                        else -> 0.3f
                    },
                    directionAngle = when (emissionDirection) {
                        DirectionalEmissionStrategy.Direction.LEFT_TO_RIGHT -> 0f
                        DirectionalEmissionStrategy.Direction.RIGHT_TO_LEFT -> 180f
                        DirectionalEmissionStrategy.Direction.TOP_TO_BOTTOM -> 90f
                        DirectionalEmissionStrategy.Direction.BOTTOM_TO_TOP -> 270f
                    },
                    directionVariance = 20f,
                    turbulenceStrength = 0.2f
                )
            )
            .withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            )
            .withParticleLifetime(800.milliseconds, 1400.milliseconds)
            .withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
            .build()
    }

    /**
     * Disintegration effect specifically optimized for text.
     * Makes text break apart into smaller particles.
     */
//    fun disintegrationText(): ParticleEffect {
//        return EffectBuilder()
//            .withName("Text Disintegration")
//            .withDuration(1500.milliseconds)
//            .withEmissionStrategy(
//                RandomEmissionStrategy(
//                    randomnessMode = RandomEmissionStrategy.RandomnessMode.CHAOTIC
//                )
//            )
//            .withPhysicsStrategy(
//                DriftPhysics(
//                    horizontalStrength = 0.6f,
//                    verticalStrength = 0.6f,
//                    directionAngle = -45f,
//                    directionVariance = 45f,
//                    gravity = 0.05f,
//                    turbulenceStrength = 0.3f
//                )
//            )
//            .withAppearanceStrategy(
//                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
//            )
//            .withAppearanceStrategy(
//                ScaleStrategy(
//                    scaleMode = ScaleStrategy.ScaleMode.SHRINK,
//                    minScale = 0.2f,
//                    maxScale = 1.0f
//                )
//            )
//            .withAppearanceStrategy(
//                RotationStrategy(
//                    rotationSpeed = 90f,
//                    randomDirection = true
//                )
//            )
//            .withParticleLifetime(800.milliseconds, 1400.milliseconds)
//            .withParticleDensity(7) // Higher density for text
//            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
//            .build()
//    }


}

